package com.mineinabyss.mobzy.spawning

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.mineinabyss.geary.components.KeepArchetype
import com.mineinabyss.geary.components.RequestCheck
import com.mineinabyss.geary.components.events.FailedCheck
import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.helpers.temporaryEntity
import com.mineinabyss.geary.papermc.bridge.config.inputs.Input
import com.mineinabyss.geary.papermc.bridge.config.inputs.Variables
import com.mineinabyss.idofront.time.inWholeTicks
import com.mineinabyss.mobzy.*
import com.mineinabyss.mobzy.spawning.vertical.VerticalSpawn
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.nield.kotlinstatistics.WeightedDice
import kotlin.random.Random

/**
 * An asynchronous repeating task that finds areas to spawn mobs in.
 *
 * ### STEP 1: Get mobs
 * Get all custom mobs in all worlds on the server, group them by entity type, and creature type.
 *
 * Getting all entities through Bukkit and grouping them every time is a little slow, but nothing
 * compared to the reading info from chunks for finding a spawn area that runs for every player group.
 *
 * ### STEP 2: Every player group picks a random chunk around them
 * Uses [toPlayerGroups] to groups nearby players together and picks a spawn around them with [randomChunkNearby]
 *
 * ### STEP 3: Calculate all mobs that can spawn in this area
 * Gets all valid [SpawnRegion]s inside a chosen location in the chunk, then gets all the spawns that can spawn
 * there.
 *
 * ### STEP 4: Pick a random valid spawn and spawn it
 * Does a weighted random decision based on each spawn's priority, and schedules a sync task that will spawn mobs in
 * the chosen region
 */
class SpawnTask {
    private val config get() = mobzySpawning.config
    private var runningTask: Job? = null
    private val regionContainer = WorldGuard.getInstance().platform.regionContainer

    fun stopTask() {
        runningTask?.cancel()
        runningTask = null
    }

    fun startTask() {
        if (runningTask != null) return
        runningTask = mobzy.plugin.launch(mobzy.plugin.asyncDispatcher) {
            while (mobzy.config.doMobSpawns) {
                try {
                    GlobalSpawnInfo.iterationNumber++
                    runSpawnTask()
                } catch (e: NoClassDefFoundError) {
                    e.printStackTrace()
                    stopTask()
                    return@launch
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }
                delay(config.spawnTaskDelay.inWholeTicks)
            }
            stopTask()
        }
    }

    private suspend fun runSpawnTask() {
        val onlinePlayers = Bukkit.getOnlinePlayers().filter { !it.isDead }
        if (onlinePlayers.isEmpty()) return

        val playerGroups = PlayerGroups.group(onlinePlayers)
        GlobalSpawnInfo.playerGroupCount = playerGroups.size

        //TODO sorted by least mobs around
        playerGroups.shuffled().forEach playerLoop@{ playerGroup ->
            val heights = playerGroup.map { it.location.y.toInt() }
            val world = playerGroup.first().world
            val heightRange = config.spawnHeightRange
            val worldHeight = world.minHeight until world.maxHeight
            val min = (heights.minOrNull()!! - heightRange).coerceIn(worldHeight)
            val max = (heights.maxOrNull()!! + heightRange).coerceIn(worldHeight)

            // Every player group picks a random chunk around them
            val chunk = PlayerGroups.randomChunkNear(playerGroup) ?: return@playerLoop
            val chunkSnapshot = chunk.chunkSnapshot
            val spawnInfo = VerticalSpawn.findGap(chunk, chunkSnapshot, min, max)

            withContext(mobzy.plugin.minecraftDispatcher) {
                val priorities = with(mobzySpawning.spawnRegistry) {
                    regionContainer.createQuery()
                        .getApplicableRegions(BukkitAdapter.adapt(spawnInfo.bottom)).regions
                        .sorted()
                        .filterWhenOverlapFlag()
                        .getMobSpawnsForRegions()
                        .associateWithTo(mutableMapOf()) {
                            (it.get<SpawnPriority>()?.priority ?: 1.0) * Random.nextDouble()
                        }
                }

                while (priorities.isNotEmpty()) {
                    val choice: GearyEntity = WeightedDice(priorities).roll()

                    //TODO this should be immutable but bukkit doesn't have an immutable location!
                    val spawnLoc = spawnInfo.getSpawnFor(choice.get() ?: SpawnPosition.GROUND)
                    val spawnCheckLoc = spawnLoc.clone().add(0.0, -1.0, 0.0)
                    val success = temporaryEntity { target -> //TODO should just pass null for target
                        target.add<KeepArchetype>()
                        target.callEvent(init = {
                            add<KeepArchetype>()
                            set(spawnInfo)
                            set(
                                Variables.Evaluated(
                                    entries = mapOf("location" to Input.of(spawnCheckLoc))
                                )
                            )
                            add<RequestCheck>()
                        }, source = choice) { !it.has<FailedCheck>() }
                    }
                    if (success) {
                        // Must spawn mobs on main thread
                        if (mobzy.plugin.isEnabled) choice.callEvent(spawnInfo, DoSpawn(spawnLoc))
                        break
                    } else priorities.remove(choice)
                }
            }
        }
    }

    /** If any of the overlapping regions is set to override, return a list with only the highest priority one,
     * otherwise the original list. */
    private fun List<ProtectedRegion>.filterWhenOverlapFlag(): List<ProtectedRegion> =
        firstOrNull { region ->
            region.flags.containsKey(mobzySpawning.worldGuardFlags.MZ_SPAWN_OVERLAP) && region.getFlag(
                mobzySpawning.worldGuardFlags.MZ_SPAWN_OVERLAP
            ) == "override"
        }
            ?.let {
                return listOf(it)
            } ?: this
}
