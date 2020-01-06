package com.offz.spigot.mobzy.spawning

import com.offz.spigot.mobzy.logError
import com.offz.spigot.mobzy.logGood
import com.offz.spigot.mobzy.spawning.MobSpawn.Companion.deserialize
import com.offz.spigot.mobzy.spawning.SpawnRegistry.regionSpawns
import com.offz.spigot.mobzy.spawning.regions.SpawnRegion
import com.offz.spigot.mobzy.toEntityType
import org.bukkit.configuration.file.FileConfiguration
import java.util.*

/**
 * @property regionSpawns A map of region names to their [SpawnRegion].
 */
object SpawnRegistry {
    private val regionSpawns: MutableMap<String, SpawnRegion> = HashMap()

    fun unregisterAll() = regionSpawns.clear()

    fun readCfg(config: FileConfiguration) {
        val regionList = config.getMapList("regions")
        for (region in regionList) {
            val name = region["name"] as String
            val spawnRegion = SpawnRegion(name)
            try {
                val spawnList = region["spawns"] as List<Map<*, *>>
                for (spawn in spawnList) {
                    spawnRegion.addSpawn(deserialize(spawn as Map<String?, Any?>))
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
                logError("Skipped region in spawns.yml because of misformatted config")
            }
            regionSpawns[name] = spawnRegion
        }
        logGood("Reloaded spawns.yml")
    }

    fun reuseMobSpawn(reusedMob: String): MobSpawn = //TODO comment this because I have no idea what it's doing
            (regionSpawns[reusedMob.substring(0, reusedMob.indexOf(':'))]
                    ?: error("Could not find registered region for $reusedMob"))
                    .getSpawnOfType(reusedMob.substring(reusedMob.indexOf(':') + 1).toEntityType())

    fun List<String>.getMobSpawnsForRegions(creatureType: String): List<MobSpawn> = this
            .filter { regionSpawns.containsKey(it) }
            .map { regionSpawns[it]!!.getSpawnsFor(creatureType) }
            .flatten()
}