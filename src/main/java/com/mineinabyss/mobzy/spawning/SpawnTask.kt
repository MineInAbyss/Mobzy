package com.mineinabyss.mobzy.spawning

import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.mobzy.*
import com.mineinabyss.mobzy.api.isCustomMob
import com.mineinabyss.mobzy.api.nms.aliases.NMSCreatureType
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.nms.entity.creatureType
import com.mineinabyss.mobzy.api.nms.entity.keyName
import com.mineinabyss.mobzy.registration.MobzyWorldguard.MZ_SPAWN_OVERLAP
import com.mineinabyss.mobzy.spawning.SpawnRegistry.getMobSpawnsForRegions
import com.mineinabyss.mobzy.spawning.SpawnTask.randomChunkSpawnNearby
import com.mineinabyss.mobzy.spawning.SpawnTask.toPlayerGroups
import com.mineinabyss.mobzy.spawning.regions.SpawnRegion
import com.okkero.skedule.BukkitSchedulerController
import com.okkero.skedule.CoroutineTask
import com.okkero.skedule.SynchronizationContext.*
import com.okkero.skedule.schedule
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.nield.kotlinstatistics.WeightedDice
import org.nield.kotlinstatistics.dbScanCluster
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.system.measureNanoTime

//TODO move into idofront
inline fun <T> printMillis(name: String, block: () -> T): T {
    var result: T? = null
    debug("$name took: ${
        measureNanoTime {
            result = block()
        } / 1000000.0
    } milliseconds")
    return result!!
}

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
 * Uses [toPlayerGroups] to groups nearby players together and picks a spawn around them with [randomChunkSpawnNearby]
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
        runningTask = mobzy.schedule {
            repeating(MobzyConfig.spawnTaskDelay)
            while (MobzyConfig.doMobSpawns) {
                try {
                    runSpawnTask()
                } catch (e: NoClassDefFoundError) {
                    e.printStackTrace()
                    stopTask()
                    return@schedule
                }
                yield()
            }

            stopTask()
        }
    }

    private suspend fun BukkitSchedulerController.runSpawnTask() {
        switchContext(SYNC)

        //TODO anything sync goes here

        switchContext(ASYNC)


        //STEP 1: Get mobs
        val onlinePlayers = Bukkit.getOnlinePlayers()
        if (onlinePlayers.isEmpty()) return

        val customMobs: List<Entity> = Bukkit.getServer().worlds.flatMap { world -> world.entities.filter { it.isCustomMob } }

        val entityTypeCounts: MutableMap<String, Int> = customMobs.toEntityTypeCounts()

        val creatureTypeCounts: MutableMap<String, Int> = customMobs.toCreatureTypeCounts()

        //don't run the task if we've hit all mob caps (might be better to run several loops for each mob type?
        if (creatureTypeCounts.none { (type, amount) -> amount < MobzyConfig.getCreatureTypeCap(NMSCreatureType.valueOf(type)) })
            return

        //STEP 2: Every player group picks a random chunk around them
        val playerGroups = onlinePlayers.toPlayerGroups()
        val playerGroupCount = playerGroups.size

        playerGroups.shuffled().forEach playerLoop@{ playerGroup ->
            val chunkSpawn: ChunkSpawn = playerGroup.randomChunkSpawnNearby ?: return@playerLoop

            val spawnArea = chunkSpawn.getSpawnArea() ?: return@playerLoop

            //STEP 3: Calculate all mobs that can spawn in this area
            val validSpawns = WeightedDice(
                    regionContainer.createQuery().getApplicableRegions(BukkitAdapter.adapt(spawnArea.bottom)).regions
                            .sorted()
                            .filterWhenOverlapFlag()
                            .getMobSpawnsForRegions()
                            .associateWith { it.getPriority(spawnArea, entityTypeCounts, creatureTypeCounts, playerGroupCount) }
                            .filterValues { it > 0 }
                            .also { if (it.isEmpty()) return@playerLoop }
            )

            //STEP 4: Pick a random valid spawn and spawn it
            val spawn = MobSpawnEvent(validSpawns.roll(), spawnArea)
//                    toSpawn.add(spawn)
            entityTypeCounts[spawn.entityType] = entityTypeCounts.getOrDefault(spawn.entityType, 0) + spawn.spawns
            creatureTypeCounts[spawn.creatureType] = creatureTypeCounts.getOrDefault(spawn.creatureType, 0) + spawn.spawns

            //spawn all the mobs we were planning to synchronously (since we can't spawn asynchronously)
            mobzy.schedule(SYNC) {
                if (mobzy.isEnabled) spawn.spawn()
            }
        }
        debug("&d&l${creatureTypeCounts.values.sum()} mobs while spawning".color())
    }

    /** Returns a random [ChunkSpawn] that is further than [MobzyConfig.minChunkSpawnRad] from all the players in this
     * list, and at least within [MobzyConfig.maxChunkSpawnRad] to one of them. */
    private val List<Entity>.randomChunkSpawnNearby: ChunkSpawn?
        get() {
            val chunk = random().location.chunk
            //TODO proper min max y for 3d space
            for (i in 0..10) {
                //get a random angle and distance, then find the side lengths of a triangle with hypotenuse length dist
                val dist = (MobzyConfig.minChunkSpawnRad..MobzyConfig.maxChunkSpawnRad).random()
                val angle = Random.nextDouble(0.0, 2 * PI)
                val newX = ceil(chunk.x + cos(angle) * dist)
                val newZ = ceil(chunk.z + sin(angle) * dist)
                if (none { distanceSquared(newX, newZ, it.location.chunk.x, it.location.chunk.z) < (MobzyConfig.minChunkSpawnRad * MobzyConfig.minChunkSpawnRad) }) {
                    val newChunk = chunk.world.getChunkAt(newX.toInt(), newZ.toInt())
                    if (!newChunk.isLoaded) continue
                    return ChunkSpawn(newChunk, 0, 255)
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

    /** Convert a list of entities to: a map of the names of creature types to the number of creatures of that type,
     * without types that have exceeded their mob cap. */
    private fun List<Entity>.toCreatureTypeCounts(): MutableMap<String, Int> =
            MobzyConfig.creatureTypes.associateWith { 0 }
                    .plus(map { it.creatureType }.groupingBy { it }.eachCount())
                    .toMutableMap()

    /** Converts a list of players to lists of groups of players within 2x spawn radius of each other. */
    private fun Collection<Entity>.toPlayerGroups(): List<List<Entity>> = groupBy { it.world }
            .flatMap { (_, players) ->
                players.dbScanCluster(
                        maximumRadius = MobzyConfig.playerGroupRadius,
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
