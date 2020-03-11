package com.mineinabyss.mobzy.spawning

import com.mineinabyss.idofront.messaging.logError
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.mobzy.Mobzy
import com.mineinabyss.mobzy.spawning.MobSpawn.Companion.deserialize
import com.mineinabyss.mobzy.spawning.SpawnRegistry.regionSpawns
import com.mineinabyss.mobzy.spawning.regions.SpawnRegion
import com.mineinabyss.mobzy.toEntityType
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.configuration.file.FileConfiguration
import java.util.*

/**
 * @property regionSpawns A map of region names to their [SpawnRegion].
 */
object SpawnRegistry {
    private val regionSpawns: MutableMap<String, SpawnRegion> = HashMap()

    fun unregisterAll() = regionSpawns.clear()

    @Suppress("UNCHECKED_CAST")
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
        logSuccess("Reloaded spawns.yml")
    }

    fun reuseMobSpawn(reusedMob: String): MobSpawn = //TODO comment this because I have no idea what it's doing
            (regionSpawns[reusedMob.substring(0, reusedMob.indexOf(':'))]
                    ?: error("Could not find registered region for $reusedMob"))
                    .getSpawnOfType(reusedMob.substring(reusedMob.indexOf(':') + 1).toEntityType())

    /**
     * Takes a list of spawn region names and converts to a list of [MobSpawn]s from those regions
     */
    fun List<ProtectedRegion>.getMobSpawnsForRegions(creatureType: String): List<MobSpawn> = this
            .filter { it.flags.containsKey(Mobzy.MZ_SPAWN_REGIONS) }
            .flatMap { it.getFlag(Mobzy.MZ_SPAWN_REGIONS)!!.split(",") }
            //up to this point, gets a list of the names of spawn areas in this region
            .filter { regionSpawns.containsKey(it) }
            .map { regionSpawns[it]!!.getSpawnsFor(creatureType) }
            .flatten()
}