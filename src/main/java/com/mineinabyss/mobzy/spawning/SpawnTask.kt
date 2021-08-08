package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.ecs.api.engine.Engine
import com.mineinabyss.geary.ecs.api.engine.temporaryEntity
import com.mineinabyss.geary.ecs.engine.iteration.QueryResult
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.ecs.query.Query
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.*
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.registration.MobzyWorldguard.MZ_SPAWN_OVERLAP
import com.mineinabyss.mobzy.spawning.SpawnDefinition.Companion.NO_LIMIT
import com.mineinabyss.mobzy.spawning.SpawnRegistry.getMobSpawnsForRegions
import com.mineinabyss.mobzy.spawning.SpawnTask.MobQuery.prefabKey
import com.mineinabyss.mobzy.spawning.regions.SpawnRegion
import com.mineinabyss.mobzy.spawning.vertical.VerticalSpawn
import com.okkero.skedule.CoroutineTask
import com.okkero.skedule.SynchronizationContext.*
import com.okkero.skedule.schedule
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedRegion
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

    object MobQuery : Query() {
        init {
            has<BukkitEntity>()
        }

        val QueryResult.prefabKey by get<PrefabKey>()
    }

    fun stopTask() {
        runningTask?.cancel()
        runningTask = null
    }

    fun startTask() {
        if (runningTask != null) return
        runningTask = mobzy.schedule(ASYNC) {
            repeating(MobzyConfig.data.spawnTaskDelay.inTicks)
            while (MobzyConfig.data.doMobSpawns) {
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
                yield()
            }
            stopTask()
        }
    }

    private fun runSpawnTask() {
        val onlinePlayers = Bukkit.getOnlinePlayers()
        if (onlinePlayers.isEmpty()) return

        val playerGroups = PlayerGroups.group(onlinePlayers)
        GlobalSpawnInfo.playerGroupCount = playerGroups.size

        val mobCounts = MobQuery.groupingBy { it.prefabKey }.eachCount()

        //TODO sorted by least mobs around
        playerGroups.shuffled().forEach playerLoop@{ playerGroup ->
            // Every player group picks a random chunk around them
            val chunk = PlayerGroups.randomChunkNear(playerGroup) ?: return@playerLoop
            Engine.temporaryEntity { spawn ->
                val spawnInfo = VerticalSpawn.findGap(chunk, 0, 255)
                val priorities = regionContainer.createQuery()
                    .getApplicableRegions(BukkitAdapter.adapt(spawnInfo.bottom)).regions
                    .sorted()
                    .filterWhenOverlapFlag()
                    .getMobSpawnsForRegions()
                    .filter {
                        it.approximateLimit == NO_LIMIT || (mobCounts[it.prefabKey]
                            ?: 0) < it.approximateLimit
                    }
                    .associateWithTo(mutableMapOf()) { it.basePriority * Random.nextDouble() }

                spawn.set(spawnInfo.bottom)
                spawn.set(spawnInfo)

                while (priorities.isNotEmpty()) {
                    val choice: SpawnDefinition = WeightedDice(priorities).roll()
                    spawn.set(choice)
                    if (choice.conditionsMet(spawn)) {
                        // Must spawn mobs in sync
                        mobzy.schedule(SYNC) {
                            if (mobzy.isEnabled) choice.spawn(spawnInfo)
                        }
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
            region.flags.containsKey(MZ_SPAWN_OVERLAP) && region.getFlag(
                MZ_SPAWN_OVERLAP
            ) == "override"
        }
            ?.let {
                return listOf(it)
            } ?: this
}
