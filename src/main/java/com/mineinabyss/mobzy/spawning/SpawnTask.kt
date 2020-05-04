package com.mineinabyss.mobzy.spawning

import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.mobzy.Mobzy.Companion.MZ_SPAWN_OVERLAP
import com.mineinabyss.mobzy.MobzyConfig
import com.mineinabyss.mobzy.api.creatureType
import com.mineinabyss.mobzy.api.isCustomMob
import com.mineinabyss.mobzy.api.isOfType
import com.mineinabyss.mobzy.api.keyName
import com.mineinabyss.mobzy.debug
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.mobzy.spawning.SpawnRegistry.getMobSpawnsForRegions
import com.mineinabyss.mobzy.spawning.vertical.SpawnArea
import com.mineinabyss.mobzy.toNMS
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.managers.RegionManager
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.scheduler.BukkitRunnable
import org.nield.kotlinstatistics.WeightedDice
import org.nield.kotlinstatistics.dbScanCluster
import kotlin.math.sign
import kotlin.system.measureNanoTime

private const val SPAWN_TRIES = 5

inline fun <T> printMillis(name: String, block: () -> T): T {
    var result: T? = null
    debug("$name took: ${measureNanoTime {
        result = block()
    } / 1000000.0} milliseconds")
    return result!!
}

@Suppress("UNREACHABLE_CODE") //TODO remove
class SpawnTask : BukkitRunnable() {
    override fun run() {
        try {
            if (!MobzyConfig.doMobSpawns) cancel().let { return }
        } catch (e: NoClassDefFoundError) {
            e.printStackTrace()
            cancel()
        }

        val container = WorldGuard.getInstance().platform.regionContainer
//        val toSpawn: MutableList<MobSpawnEvent> = mutableListOf()

        //go async
        Bukkit.getScheduler().runTaskAsynchronously(mobzy, Runnable async@{
            //STEP 1: Get entities
            //TODO get this in a smart way from all existing entities
            // do this using something that listens to entity spawns/removal
            printMillis("&6Full task".color()) {
                val customMobs = printMillis("Getting world entities".color()) {
                    Bukkit.getServer().worlds.flatMap { world -> world.entities.filter { it.isCustomMob } }
                }

                val entityTypeCounts: MutableMap<String, Int> = printMillis("Convert entity type counts") { customMobs.toEntityTypeCounts() }
                val originalCount = customMobs.size

                val creatureTypeCounts: MutableMap<String, Int> = printMillis("Convert creature type counts") { customMobs.toCreatureTypeCounts() }

                //don't run the task if we've hit all mob caps (might be better to run several loops for each mob type?
                if (creatureTypeCounts.none { (type, amount) -> amount < MobzyConfig.getMobCap(type) }) return@async

                val playerGroups = printMillis("Get player groups entity type counts") {
                    /*Bukkit.getOnlinePlayers()*/
                    customMobs.filter { it.isOfType("riko") }.toPlayerGroups()
                }
                val playerGroupCount = playerGroups.size
                //STEP 2: Every player group picks a random chunk around them
                playerGroups.shuffled().forEach playerLoop@{ players ->
                    val chunkSpawn: ChunkSpawn = players.randomChunkSpawnNearby ?: return@playerLoop
                    val spawnArea = chunkSpawn.getSpawnArea(SPAWN_TRIES) ?: return@playerLoop

                    //STEP 3: Calculate all mobs that can spawn in this area
                    val validSpawns = printMillis("Get valid spawns") {
                        WeightedDice(
                                container.createQuery().getApplicableRegions(BukkitAdapter.adapt(spawnArea.bottom)).regions
                                        .sorted()
                                        .filterWhenOverlapFlag()
                                        .getMobSpawnsForRegions()
                                        .associateWith { it.getPriority(spawnArea, entityTypeCounts, creatureTypeCounts, playerGroupCount) }
                                        .filterValues { it > 0 }
                                        .also { if (it.isEmpty()) return@playerLoop }
                        )
                    }

                    //STEP 4: Pick a random valid spawn and spawn it
                    val spawn = MobSpawnEvent(validSpawns.roll(), spawnArea)
//                    toSpawn.add(spawn)
                    entityTypeCounts[spawn.entityType] = entityTypeCounts.getOrDefault(spawn.entityType, 0) + spawn.spawns
                    creatureTypeCounts[spawn.creatureType] = creatureTypeCounts.getOrDefault(spawn.creatureType, 0) + spawn.spawns

                    //spawn all the mobs we were planning to synchronously (since we can't spawn asynchronously)
                    Bukkit.getScheduler().runTask(mobzy, Runnable syncSpawnTask@{
                        if (!mobzy.isEnabled) return@syncSpawnTask
                        spawn.spawn()
//                        toSpawn.forEach { it.spawn() }
//                    after we've hit the mob cap, print mob count
                       /* if (toSpawn.size > 0) */debug("&d&l${creatureTypeCounts.values.sum()} mobs while spawning".color())
                    })
                }
            }
        })
    }

    /**
     * Returns a random [ChunkSpawn] that is further than [MobzyConfig.minChunkSpawnRad] from all the players in this
     * list, and at least within [MobzyConfig.maxChunkSpawnRad] to one of them.
     * TODO make it actually do that
     */
    private val List<Entity>.randomChunkSpawnNearby: ChunkSpawn?
        get() {
            val chunk = random().location.chunk
            fun randomOffset() = randomSign * (MobzyConfig.minChunkSpawnRad..MobzyConfig.maxChunkSpawnRad).random()
            var newX: Int
            var newZ: Int
            //TODO proper min max y
            for (i in 0..10) {
                newX = chunk.x + randomOffset()
                newZ = chunk.z + randomOffset()
                if (none { (newX to newZ).distanceSquared(it.location.chunk.x to it.location.chunk.z) < (MobzyConfig.minChunkSpawnRad * MobzyConfig.minChunkSpawnRad) }) {
                    val newChunk = chunk.world.getChunkAt(newX, newZ)
                    return ChunkSpawn(newChunk, 0, 256)
                }
            }
            return null
        }

    private fun Pair<Number, Number>.distanceSquared(other: Pair<Number, Number>): Double =
            this.first.toDouble() * other.first.toDouble() + this.second.toDouble() * other.second.toDouble()

    /** Convert a list of entities to: a map of the names of creature types to the number of creatures of that type,
     * without types that have exceeded their mob cap. */
    private fun List<Entity>.toCreatureTypeCounts(): MutableMap<String, Int> =
            MobzyConfig.creatureTypes.associateWith { 0 }
                    .plus(map { it.toNMS().creatureType }.groupingBy { it }.eachCount())
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

    /** The list of mob spawns based on WorldGuard regions, then remove all impossible spawns, and make entity weighted
     * decision on the spawn. */
    private fun RegionManager.getWorldGuardRegions(spawnArea: SpawnArea) =
            getApplicableRegions(BukkitAdapter.asBlockVector(spawnArea.bottom)).regions.sorted()

    private val randomSign get() = ((0..1).random() - 0.5).sign.toInt()
}