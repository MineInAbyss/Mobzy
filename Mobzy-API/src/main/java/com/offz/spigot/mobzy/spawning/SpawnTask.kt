package com.offz.spigot.mobzy.spawning

import com.offz.spigot.mobzy.*
import com.offz.spigot.mobzy.Mobzy.Companion.MZ_SPAWN_OVERLAP
import com.offz.spigot.mobzy.Mobzy.Companion.MZ_SPAWN_REGIONS
import com.offz.spigot.mobzy.spawning.SpawnRegistry.getMobSpawnsForRegions
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import org.apache.commons.lang.mutable.MutableInt
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

//TODO convert to kotlin
class SpawnTask : BukkitRunnable() {
    private val config: MobzyConfig = mobzy.mobzyConfig

    override fun run() { //FIXME getNearbyEntities is no longer async
        try { //run checks asynchronously
            if (!MobzyConfig.doMobSpawns) {
                cancel()
                return
            }

            val container = WorldGuard.getInstance().platform.regionContainer

            for (p in Bukkit.getOnlinePlayers()) {
                val skippedPlayers: MutableList<UUID> = mutableListOf()
                val closePlayers: MutableList<Location> = mutableListOf(p.location)

                //if this player has been registered as close to another, do not make additional spawns
                if (skippedPlayers.contains(p.uniqueId)) continue

                val regions = container[BukkitAdapter.adapt(p.world)] ?: continue

                //decide spawns around player
                val toSpawn: MutableList<MobSpawnEvent> = mutableListOf()
                val originalMobCount: MutableMap<String, MutableInt> = hashMapOf()
                config.creatureTypes.forEach { type -> originalMobCount[type] = MutableInt(0) }
                val totalMobs = MutableInt(0)

                //go through entities around player, adding nearby players to a list
                val radius = MobzyConfig.spawnSearchRadius
                for (entity in p.getNearbyEntities(radius, radius, radius))
                    if (entity.isCustomMob) {
                        for (type in config.creatureTypes) {
                            if (entity.isOfCreatureType(type)) {
                                originalMobCount[type]!!.increment()
                                totalMobs.increment()
                                break
                            }
                        }
                    } else if (entity.type == EntityType.PLAYER) {
                        skippedPlayers.add(entity.uniqueId)
                        closePlayers.add(entity.location)
                    }

                //if all of the mob caps have been hit, we can stop here
                if (originalMobCount.entries.all { it.value.toInt() > MobzyConfig.getMobCap(it.key) }) return

                Bukkit.getScheduler().runTaskAsynchronously(mobzy, Runnable {
                    //duplicate contents of original into mobCount
                    val mobCount: MutableMap<String, MutableInt> = mutableMapOf()
                    originalMobCount.entries.forEach { mobCount[it.key] = MutableInt(it.value) }

                    //STEP 1: Generate array of ChunkSpawns around player, and invalidate the ones that are empty
                    val spawnChunkGrid = SpawnChunkGrid(closePlayers, MobzyConfig.minChunkSpawnRad, MobzyConfig.maxChunkSpawnRad)

                    mobTypeLoop@ for (type in config.creatureTypes) {
                        val chunkSpawns = spawnChunkGrid.shuffledSpawns
                        for (chunkSpawn in chunkSpawns) {
                            //if mob cap of that specific mob has been reached, skip it
                            if ((mobCount[type]!!).toInt() > MobzyConfig.getMobCap(type)) continue@mobTypeLoop

                            //STEP 2: Each chunk tries to choose one area inside it for which to attempt a spawn
                            val spawnArea = chunkSpawn.getSpawnArea(SPAWN_TRIES) ?: continue
                            //TODO Figure out something for determining the region in different spots in spawn area.
                            // It'll need each mob to decide on a position first, then run it through here.
                            // Maybe this entire system needs reworking

                            //STEP 3: Pick mob to spawn
                            // get the list of mob spawns based on WorldGuard regions, then remove all impossible spawns,
                            // and make entity weighted decision on the spawn
                            val inRegions = regions.getApplicableRegions(BukkitAdapter.asBlockVector(spawnArea.bottom)).regions.toMutableList().also { it.sort() }

                            //if any of the overlapping regions is set to override, the highest priority one will set only its spawns as viable
                            inRegions.firstOrNull { region -> region.flags.containsKey(MZ_SPAWN_OVERLAP) && region.getFlag(MZ_SPAWN_OVERLAP) == "override" }
                                    ?.let {
                                        inRegions.clear()
                                        inRegions.add(it)
                                    }

                            val validSpawns = RandomCollection<MobSpawn>()
                            inRegions.filter { region -> region.flags.containsKey(MZ_SPAWN_REGIONS) }
                                    .map { region -> region.getFlag(MZ_SPAWN_REGIONS)!!.split(",") }
                                    .flatten() //up to this point, gets a list of the names of spawn areas in this region
                                    .getMobSpawnsForRegions(type) //convert to a list of MobSpawns
                                    .forEach { validSpawns.add(it.getPriority(spawnArea, toSpawn), it) } //add the valid spawns to validSpawns

                            if (validSpawns.isEmpty) continue
                            //weighted random decision of valid spawn
                            val spawn = MobSpawnEvent(validSpawns.next(), spawnArea)
                            toSpawn.add(spawn)
                            //increment the number of existing mobs by the number we want to spawn
                            mobCount[type]!!.add(spawn.spawns)
                        }
                    }

                    //spawn all the mobs we were planning to synchronously (since we can't spawn asynchronously)
                    Bukkit.getScheduler().runTask(mobzy, Runnable {
                        for (spawn in toSpawn) spawn.spawn()
                        //after we've hit the mob cap, print mob count
                        if (toSpawn.size > 0) {
                            debug("${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}$totalMobs mobs before")
                            mobCount.entries.filter { it.value.toInt() - originalMobCount[it.key]!!.toInt() > 0 }
                                    .forEach { debug("${ChatColor.LIGHT_PURPLE}${(it.value.toInt() - originalMobCount[it.key]!!.toInt())} ${it.key}s spawned") }
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
}