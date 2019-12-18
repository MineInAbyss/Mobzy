package com.offz.spigot.mobzy.spawning

import com.offz.spigot.mobzy.CustomType.Companion.getType
import com.offz.spigot.mobzy.logError
import com.offz.spigot.mobzy.logGood
import com.offz.spigot.mobzy.spawning.MobSpawn.Companion.deserialize
import com.offz.spigot.mobzy.spawning.regions.SpawnRegion
import net.minecraft.server.v1_15_R1.Entity
import org.bukkit.configuration.file.FileConfiguration
import java.util.*

object SpawnRegistry {
    private val regionSpawns: MutableMap<String?, SpawnRegion?> = HashMap()

    fun unregisterAll() = regionSpawns.clear()

    fun readCfg(config: FileConfiguration) {
        val regionList = config.getMapList("regions")
        for (region in regionList) {
            val name = region["name"] as String
            val spawnRegion = SpawnRegion(name)
            try {
                val spawnList = region["spawns"] as List<Map<*, *>>
                for (spawn in spawnList) {

                    //create a new builder from the MobSpawn found inside of an already created layer
                    /*if (spawn.containsKey("reuse")) {
                        val reusedMob = spawn["reuse"] as String?
                        mobSpawn = MobSpawn.newBuilder(getReusedBuilder(reusedMob))
                    } else if (!spawn.containsKey("mob")) break
                    */ //FIXME add back reuse functionality
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

    fun reuseMobSpawn(reusedMob: String): MobSpawn = //TODO comment this out because I have no idea what it's doing
            regionSpawns[reusedMob.substring(0, reusedMob.indexOf(':'))]!!
                    .getSpawnOfType(getType(reusedMob.substring(reusedMob.indexOf(':') + 1)))

    fun getMobSpawnsForRegions(regionIDs: List<String?>, mobType: Class<out Entity?>?) = regionIDs
            .filter { regionSpawns.containsKey(it) }
            .map { regionSpawns[it]!!.getSpawnsFor(mobType) }
            .flatten() //TODO make sure this is working
}