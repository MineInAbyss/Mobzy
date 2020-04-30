package com.mineinabyss.mobzy.spawning

import com.mineinabyss.mobzy.Mobzy
import com.mineinabyss.mobzy.registration.MobzyTypes
import com.mineinabyss.mobzy.spawning.SpawnRegistry.regionSpawns
import com.mineinabyss.mobzy.spawning.regions.SpawnRegion
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import java.util.*

/**
 * @property regionSpawns A map of region names to their [SpawnRegion].
 */
object SpawnRegistry {
    private val regionSpawns: MutableMap<String, SpawnRegion> = HashMap()
//    private val reusableMobSpawns: MutableMap<String, MobSpawn> = HashMap()

    fun unregisterSpawns() = regionSpawns.clear()

//    operator fun plusAssign(config: SpawnConfiguration) {
//        regionSpawns += config.info.regions.associateBy { region -> region.name }
//    }

    operator fun plusAssign(region: SpawnRegion) {
        regionSpawns += region.name to region
    }

    fun reuseMobSpawn(reusedMob: String): MobSpawn = //TODO comment this because I have no idea what it's doing
            (regionSpawns[reusedMob.substring(0, reusedMob.indexOf(':'))]
                    ?: error("Could not find registered region for $reusedMob"))
                    .getSpawnOfType(MobzyTypes[reusedMob.substring(reusedMob.indexOf(':') + 1)])

    /**
     * Takes a list of spawn region names and converts to a list of [MobSpawn]s from those regions
     */
    fun List<ProtectedRegion>.getMobSpawnsForRegions(/*creatureType: String*/): List<MobSpawn> = this
            .filter { it.flags.containsKey(Mobzy.MZ_SPAWN_REGIONS) }
            .flatMap { it.getFlag(Mobzy.MZ_SPAWN_REGIONS)!!.split(",") }
            //up to this point, gets a list of the names of spawn areas in this region
            .mapNotNull { regionSpawns[it]?.spawns/*.getSpawnsFor(creatureType)*/ }
            .flatten()
}