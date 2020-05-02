package com.mineinabyss.mobzy.spawning

import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.mobzy.Mobzy.Companion.MZ_SPAWN_OVERLAP
import com.mineinabyss.mobzy.MobzyConfig
import com.mineinabyss.mobzy.api.creatureType
import com.mineinabyss.mobzy.api.isCustomMob
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
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.sign
import kotlin.system.measureNanoTime

private const val SPAWN_TRIES = 5

inline fun <T> printTimeMillis(name: String, block: () -> T): T {
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
        val toSpawn: MutableList<MobSpawnEvent> = mutableListOf()

        //go async
        Bukkit.getScheduler().runTaskAsynchronously(mobzy, Runnable {
            //STEP 1: Get entities
            //TODO get this in a smart way from all existing entities
            // do this using something that listens to entity spawns/removal
            val customMobs = printTimeMillis("Getting world etities") {
                Bukkit.getServer().worlds.flatMap { world -> world.entities.filter { it.isCustomMob } }
            }

            Bukkit.getOnlinePlayers().forEach { player ->
//                val regionManager = container[BukkitAdapter.adapt(player.world)] ?: return@forEach

                val entityTypeCounts = customMobs.toEntityTypeCounts()
                val originalCount = customMobs.size

                //STEP 2: Each chunk tries to choose one area inside it for which to attempt a spawn

                customMobs.toCreatureTypeCounts().forEach spawnPerType@{ (type, count) ->
                    var newCount = count
                    val creatureTypeCap = MobzyConfig.getMobCap(type)
                    if (newCount > creatureTypeCap)
                        return@spawnPerType

                    val chunkSpawn: ChunkSpawn = player.randomChunkNearby

                    val spawnArea = chunkSpawn.getSpawnArea(SPAWN_TRIES) ?: return@spawnPerType

                    //STEP 3: Pick mob to spawn
                    val validSpawns = RandomCollection<MobSpawn>()

                    //TODO revert if this doesn't work
                    container.createQuery().getApplicableRegions(BukkitAdapter.adapt(spawnArea.bottom)).regions.sorted()
//                    regionManager.getWorldGuardRegions(spawnArea)
                            .filterWhenOverlapFlag()
                            .getMobSpawnsForRegions()
                            .forEach { validSpawns.add(it.getPriority(spawnArea, entityTypeCounts), it) }

                    if (validSpawns.isEmpty) return@spawnPerType
                    //weighted random decision of valid spawn
                    val spawn = MobSpawnEvent(validSpawns.next(), spawnArea)
                    toSpawn.add(spawn)
                    entityTypeCounts[spawn.entityType] = entityTypeCounts.getOrDefault(spawn.entityType, 0).plus(spawn.spawns)

                    newCount += spawn.spawns //increment the number of existing mobs by the number we want to spawn
                }

                //spawn all the mobs we were planning to synchronously (since we can't spawn asynchronously)
                Bukkit.getScheduler().runTask(mobzy, Runnable syncSpawnTask@{
                    if (!mobzy.isEnabled) return@syncSpawnTask
                    toSpawn.forEach { it.spawn() }
                    //after we've hit the mob cap, print mob count
                    if (toSpawn.size > 0) debug("&d&l$originalCount mobs before".color())
                })
            }
        })
    }

    private val Player.randomChunkNearby: ChunkSpawn
        get() {
            val loc = this.location
            val chunk = player?.location?.chunk ?: error("Player is not in a chunk")
            fun randomOffset() = randomSign * (MobzyConfig.minChunkSpawnRad..MobzyConfig.maxChunkSpawnRad).random()
            val newX = chunk.x + randomOffset()
            val newZ = chunk.z + randomOffset()
            val newChunk = this.world.getChunkAt(newX, newZ)
            return ChunkSpawn(newChunk, 0, 256) //TODO proper min max y
        }

    /** Convert a list of entities to: a map of the names of creature types to the number of creatures of that type,
     * without types that have exceeded their mob cap. */
    private fun List<Entity>.toCreatureTypeCounts(): Map<String, Int> =
            MobzyConfig.creatureTypes.associateWith { 0 }
                    .plus(map { it.toNMS().creatureType }.groupingBy { it }.eachCount())
                    .filter { (type, count) -> count < MobzyConfig.getMobCap(type) }

    /** Converts a list of players to lists of groups of players within 2x spawn radius of each other. */
    /*private fun List<Player>.toPlayerGroups(): List<List<Player>> = groupBy { it.world }
            .flatMap { (_, players) ->
                players.dbScanCluster(
                        maximumRadius = mobzyConfig.spawnSearchRadius,
                        minPoints = 0,
                        xSelector = { it.location.x },
                        ySelector = { it.location.z }
                )
            }.map { it.points }*/

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