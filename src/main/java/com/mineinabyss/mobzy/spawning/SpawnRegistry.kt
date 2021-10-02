package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.ecs.engine.iteration.QueryResult
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.ecs.prefab.PrefabManager
import com.mineinabyss.geary.ecs.query.Query
import com.mineinabyss.mobzy.configuration.SpawnConfig
import com.mineinabyss.mobzy.registration.MobzyWorldguard.MZ_SPAWN_REGIONS
import com.mineinabyss.mobzy.spawning.SpawnRegistry.regionSpawns
import com.mineinabyss.mobzy.spawning.regions.SpawnRegion
import com.sk89q.worldguard.protection.regions.ProtectedRegion

/**
 * A singleton for keeping track of registered [SpawnRegion]s. Used for the mob spawning system
 *
 * @property regionSpawns A map of region names to their [SpawnRegion].
 */
object SpawnRegistry {
    private val regionSpawns: MutableMap<String, SpawnRegion> = HashMap()

    /** Clears [regionSpawns] */
    fun unregisterSpawns() = regionSpawns.clear()

    object SpawnConfigs : Query() {
        val QueryResult.config by get<SpawnConfig>()
    }

    fun reloadSpawns() {
        unregisterSpawns()
        SpawnConfigs.apply {
            forEach { PrefabManager.reread(it.entity) }
        }
    }

    /** Register the specified [SpawnRegion] */
    operator fun plusAssign(region: SpawnRegion) {
        regionSpawns += region.name to region
    }

    /**
     * Finds a [SpawnDefinition] in the form `"RegionName:MobName"` and will find the first mob of that type inside the region
     * of that name.
     */
    fun findMobSpawn(spawn: String): SpawnDefinition {
        val (regionName, prefabName) = spawn.split('.')
        return (regionSpawns[regionName] ?: error("Could not find registered region for $spawn"))
            .getSpawnOfType(PrefabKey.of(prefabName))
    }

    /** Takes a list of spawn region names and converts to a list of [SpawnDefinition]s from those regions */
    fun List<ProtectedRegion>.getMobSpawnsForRegions(): List<SpawnDefinition> = this
        .filter { it.flags.containsKey(MZ_SPAWN_REGIONS) }
        .flatMap { it.getFlag(MZ_SPAWN_REGIONS)!!.split(",") }
        //up to this point, gets a list of the names of spawn areas in this region
        .mapNotNull { regionSpawns[it]?.spawns }
        .flatten()
}
