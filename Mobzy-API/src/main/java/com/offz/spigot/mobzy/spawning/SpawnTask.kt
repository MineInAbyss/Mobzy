package com.offz.spigot.mobzy.spawning

import com.mineinabyss.idofront.logVal
import com.mineinabyss.idofront.toNMS
import com.offz.spigot.mobzy.*
import com.offz.spigot.mobzy.Mobzy.Companion.MZ_SPAWN_OVERLAP
import com.offz.spigot.mobzy.spawning.SpawnRegistry.getMobSpawnsForRegions
import com.offz.spigot.mobzy.spawning.vertical.SpawnArea
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.managers.RegionManager
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.nield.kotlinstatistics.dbScanCluster

class SpawnTask : BukkitRunnable() {
    private val config: MobzyConfig = mobzy.mobzyConfig

    override fun run() {
        try { //run checks asynchronously
            if (!MobzyConfig.doMobSpawns) {
                cancel()
                return
            }

            val container = WorldGuard.getInstance().platform.regionContainer

            Bukkit.getOnlinePlayers().toList().toPlayerGroups().logVal("Player Groups ").forEach { (center, playerGroup) ->
                val regionManager = container[BukkitAdapter.adapt(playerGroup[0].world)] ?: return@forEach
                val toSpawn: MutableList<MobSpawnEvent> = mutableListOf()

                //STEP 1: Generate array of ChunkSpawns around player group
                val spawnChunkGrid = SpawnChunkGrid(playerGroup.map { it.location }, MobzyConfig.minChunkSpawnRad, MobzyConfig.maxChunkSpawnRad)

                val customMobs = spawnChunkGrid.allChunks.getCustomMobs()

                Bukkit.getScheduler().runTaskAsynchronously(mobzy, Runnable {
                    val entityTypeCounts = customMobs.toEntityTypeCounts()
                    val originalCount = customMobs.size

                    //STEP 2: Each chunk tries to choose one area inside it for which to attempt a spawn
                    customMobs.toCreatureTypeCounts().forEach spawnPerType@{ (type, count) ->
                        var newCount = count
                        spawnChunkGrid.shuffledSpawns().forEach spawnLoop@{ chunkSpawn ->
                            if (newCount > MobzyConfig.getMobCap(type)) return@spawnPerType
                            val spawnArea = chunkSpawn.getSpawnArea(SPAWN_TRIES) ?: return@spawnLoop

                            //STEP 3: Pick mob to spawn
                            val validSpawns = RandomCollection<MobSpawn>()
                            regionManager.getWorldGuardRegions(spawnArea)
                                    .filterWhenOverlapFlag()
                                    .getMobSpawnsForRegions(type) //convert to a list of MobSpawns
                                    .forEach { validSpawns.add(it.getPriority(spawnArea, toSpawn, entityTypeCounts), it) }

                            if (validSpawns.isEmpty) return@spawnLoop
                            //weighted random decision of valid spawn
                            val spawn = MobSpawnEvent(validSpawns.next(), spawnArea)
                            toSpawn.add(spawn)

                            newCount += spawn.spawns //increment the number of existing mobs by the number we want to spawn
                        }
                    }

                    //spawn all the mobs we were planning to synchronously (since we can't spawn asynchronously)
                    Bukkit.getScheduler().runTask(mobzy, Runnable syncSpawnTask@{
                        if (!mobzy.isEnabled) return@syncSpawnTask
                        toSpawn.forEach { it.spawn() }
                        //after we've hit the mob cap, print mob count
                        if (toSpawn.size > 0) {
                            debug("&d&l$originalCount mobs before", '&')
                            //TODO log the number
//                            if (newCount > count)
//                                logInfo("&d${newCount - count} ${type}s spawned", '&')
                        }
                    })
                })
            }
        } catch (e: NoClassDefFoundError) {
            e.printStackTrace()
            cancel()
        }
    }

    companion object {
        private const val SPAWN_TRIES = 5
    }

    /**
     * @return A map of the names of creature types to the number of creatures of that type, without types that have
     * exceeded the mob cap.
     */
    private fun List<Entity>.toCreatureTypeCounts(): Map<String, Int> =
            config.creatureTypes.associateWith { 0 }
                    .plus(map { it.toNMS().creatureType }.groupingBy { it }.eachCount())
                    .filter { (type, count) -> count < MobzyConfig.getMobCap(type) }


    private fun List<Player>.toPlayerGroups() = groupBy { it.world }
            .flatMap { (_, players) ->
                players.dbScanCluster(
                        maximumRadius = MobzyConfig.spawnSearchRadius,
                        minPoints = 0,
                        xSelector = { it.location.x },
                        ySelector = { it.location.y }
                )
            }.associate { it.center to it.points }

    private fun List<Chunk>.getCustomMobs() = flatMap { chunks -> chunks.entities.filter { it.isCustomMob }.toList() }

    private fun List<Entity>.toEntityTypeCounts(): Map<String, Int> =
            map { it.toNMS().entityType.name }.groupingBy { it }.eachCount()

    /**
     * @return If any of the overlapping regions is set to override, return a list with only the highest priority one,
     * otherwise the original list
     */
    private fun List<ProtectedRegion>.filterWhenOverlapFlag(): List<ProtectedRegion> =
            firstOrNull { region -> region.flags.containsKey(MZ_SPAWN_OVERLAP) && region.getFlag(MZ_SPAWN_OVERLAP) == "override" }
                    ?.let {
                        return listOf(it)
                    } ?: this

    /**
     * @return the list of mob spawns based on WorldGuard regions, then remove all impossible spawns,
     * and make entity weighted decision on the spawn
     */
    private fun RegionManager.getWorldGuardRegions(spawnArea: SpawnArea) =
            getApplicableRegions(BukkitAdapter.asBlockVector(spawnArea.bottom)).regions.sorted()
}