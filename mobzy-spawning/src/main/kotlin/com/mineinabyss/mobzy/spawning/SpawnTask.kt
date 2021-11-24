package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.mobzy.*
import com.mineinabyss.mobzy.spawning.SpawnRegistry.getMobSpawnsForRegions
import com.mineinabyss.mobzy.spawning.WorldGuardSpawnFlags.MZ_SPAWN_OVERLAP
import com.mineinabyss.mobzy.spawning.conditions.CheckSpawn
import com.mineinabyss.mobzy.spawning.vertical.VerticalSpawn
import com.okkero.skedule.CoroutineTask
import com.okkero.skedule.SynchronizationContext.*
import com.okkero.skedule.schedule
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
object SpawnTask {
    private var runningTask: CoroutineTask? = null

    private val regionContainer = WorldGuard.getInstance().platform.regionContainer

    fun stopTask() {
        runningTask?.cancel()
        runningTask = null
    }

    fun startTask() {
        if (runningTask != null) return
        runningTask = mobzy.schedule(ASYNC) {
            while (mobzyConfig.doMobSpawns) {
                try {
                    GlobalSpawnInfo.iterationNumber++
                    runSpawnTask()
                } catch (e: NoClassDefFoundError) {
                    e.printStackTrace()
                    stopTask()
                    return@schedule
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }
                waitFor(mobzyConfig.spawnTaskDelay.inTicks)
            }
            stopTask()
        }
    }

    private fun runSpawnTask() {
        val onlinePlayers = Bukkit.getOnlinePlayers()
        if (onlinePlayers.isEmpty()) return

        val playerGroups = PlayerGroups.group(onlinePlayers)
        GlobalSpawnInfo.playerGroupCount = playerGroups.size

        //TODO sorted by least mobs around
        //TODO i think it cant find slimjar classes cause not loaded into system classloader
        playerGroups.shuffled().forEach playerLoop@{ playerGroup ->
            val heights = playerGroup.map { it.location.y.toInt() }
            val world = playerGroup.first().world
            val heightRange = mobzyConfig.spawnHeightRange
            val worldHeight = world.minHeight until world.maxHeight
            val min = (heights.minOrNull()!! - heightRange).coerceIn(worldHeight)
            val max = (heights.maxOrNull()!! + heightRange).coerceIn(worldHeight)

            // Every player group picks a random chunk around them
            val chunk = PlayerGroups.randomChunkNear(playerGroup) ?: return@playerLoop
            val chunkSnapshot = chunk.chunkSnapshot
            val spawnInfo = VerticalSpawn.findGap(chunk, chunkSnapshot, min, max)
            val spawnEvent = SpawnEvent(spawnInfo)
            val priorities = regionContainer.createQuery()
                .getApplicableRegions(BukkitAdapter.adapt(spawnInfo.bottom)).regions
                .sorted()
                .filterWhenOverlapFlag()
                .getMobSpawnsForRegions()
                .associateWithTo(mutableMapOf()) {
                    (it.get<SpawnPriority>()?.priority ?: 1.0) * Random.nextDouble()
                }

            while (priorities.isNotEmpty()) {
                val choice: GearyEntity = WeightedDice(priorities).roll()
                val verify = CheckSpawn(spawnInfo)
                choice.callEvent(verify)
                if (verify.success) {
                    // Must spawn mobs in sync
                    mobzy.schedule(SYNC) {
                        if (mobzy.isEnabled) choice.callEvent(spawnEvent)
                    }
                    break
                } else priorities.remove(choice)
            }
        }
    }

    /** If any of the overlapping regions is set to override, return a list with only the highest priority one,
     * otherwise the original list. */
    private fun List<ProtectedRegion>.filterWhenOverlapFlag(): List<ProtectedRegion> =
        firstOrNull { region -> region.flags.containsKey(MZ_SPAWN_OVERLAP) && region.getFlag(MZ_SPAWN_OVERLAP) == "override" }
            ?.let {
                return listOf(it)
            } ?: this
}
