package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.ecs.api.engine.Engine
import com.mineinabyss.geary.ecs.api.engine.temporaryEntity
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.nms.entity.keyName
import com.mineinabyss.mobzy.*
import com.mineinabyss.mobzy.registration.MobzyWorldguard.MZ_SPAWN_OVERLAP
import com.mineinabyss.mobzy.spawning.SpawnRegistry.getMobSpawnsForRegions
import com.mineinabyss.mobzy.spawning.SpawnTask.randomChunkNearby
import com.mineinabyss.mobzy.spawning.SpawnTask.toPlayerGroups
import com.mineinabyss.mobzy.spawning.regions.SpawnRegion
import com.mineinabyss.mobzy.spawning.vertical.VerticalSpawn
import com.okkero.skedule.CoroutineTask
import com.okkero.skedule.SynchronizationContext.*
import com.okkero.skedule.schedule
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.entity.Entity
import org.nield.kotlinstatistics.WeightedDice
import org.nield.kotlinstatistics.dbScanCluster
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

        val playerGroups = onlinePlayers.toPlayerGroups()
        GlobalSpawnInfo.playerGroupCount = playerGroups.size

        //TODO sorted by least mobs around
        playerGroups.shuffled().forEach playerLoop@{ playerGroup ->
            // Every player group picks a random chunk around them
            val chunkSpawn: Chunk = playerGroup.randomChunkNearby ?: return@playerLoop
            Engine.temporaryEntity { spawn ->
                val spawnInfo = VerticalSpawn.findGap(chunkSpawn, 0, 255)
                val priorities = regionContainer.createQuery()
                    .getApplicableRegions(BukkitAdapter.adapt(spawnInfo.bottom)).regions
                    .sorted()
                    .filterWhenOverlapFlag()
                    .getMobSpawnsForRegions()
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

    private infix fun Int.`+-`(other: Int) =
        this + setOf(-1, 1).random() * other

    /** Returns a random [Chunk] that is further than [MobzyConfig.Data.minChunkSpawnRad] from all the players in this
     * list, and at least within [MobzyConfig.Data.maxChunkSpawnRad] to one of them. */
    private val List<Entity>.randomChunkNearby: Chunk?
        get() {
            val chunk = random().location.chunk
            //TODO proper min max y for 3d space
            for (i in 0..10) {
                //get a random angle and distance, then find the side lengths of a triangle with hypotenuse length dist
                val distRange = (MobzyConfig.data.minChunkSpawnRad..MobzyConfig.data.maxChunkSpawnRad)
                val distX = distRange.random()
                val distZ = distRange.random()
                val newX = chunk.x `+-` distX
                val newZ = chunk.z `+-` distZ
                if (none {
                        val entityChunk = it.location.chunk
                        distanceSquared(newX, newZ, entityChunk.x, entityChunk.z) <
                                (MobzyConfig.data.minChunkSpawnRad * MobzyConfig.data.minChunkSpawnRad)
                    }) {
                    val newChunk = chunk.world.getChunkAt(newX, newZ)
                    if (!newChunk.isLoaded) continue
                    return newChunk
                }
            }
            return null
        }

    /** Gets the distance squared between between two points */
    private fun distanceSquared(x: Number, z: Number, otherX: Number, otherZ: Number): Double {
        val dx = (x.toDouble() + otherX.toDouble())
        val dz = (z.toDouble() + otherZ.toDouble())
        return dx * dx + dz * dz
    }

    /** Converts a list of players to lists of groups of players within 2x spawn radius of each other. */
    private fun Collection<Entity>.toPlayerGroups(): List<List<Entity>> = groupBy { it.world }
        .flatMap { (_, players) ->
            players.dbScanCluster(
                maximumRadius = MobzyConfig.data.playerGroupRadius,
                minPoints = 0,
                xSelector = { it.location.x },
                ySelector = { it.location.z }
            )
        }.map { it.points }

    /** Converts a list of entities to a map of entity types to the amount of entities of that type. */
    private fun List<Entity>.toEntityTypeCounts(): MutableMap<String, Int> =
        map { it.toNMS().entityType.keyName }.groupingBy { it }.eachCountTo(mutableMapOf())

    /** If any of the overlapping regions is set to override, return a list with only the highest priority one,
     * otherwise the original list. */
    private fun List<ProtectedRegion>.filterWhenOverlapFlag(): List<ProtectedRegion> =
        firstOrNull { region -> region.flags.containsKey(MZ_SPAWN_OVERLAP) && region.getFlag(MZ_SPAWN_OVERLAP) == "override" }
            ?.let {
                return listOf(it)
            } ?: this
}
