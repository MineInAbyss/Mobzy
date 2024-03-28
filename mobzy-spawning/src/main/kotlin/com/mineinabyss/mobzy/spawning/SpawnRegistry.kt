package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.geary.prefabs.prefabs
import com.mineinabyss.geary.systems.builders.cachedQuery
import com.mineinabyss.geary.systems.query.GearyQuery
import com.mineinabyss.geary.systems.query.Query
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedRegion

/**
 * A singleton for keeping track of registered [SpawnRegion]s. Used for the mob spawning system
 *
 * @property regionSpawns A map of region names to their [SpawnRegion].
 */
class SpawnRegistry {
    private val prefabLoader get() = prefabs.loader

    private val spawnsWithWGRegion = geary.cachedQuery(object : Query() {
        val parentRegions by get<WGRegions>()
        val spawn by get<SpawnType>()
    })

    private val regionContainer = WorldGuard.getInstance().platform.regionContainer
    private var regionSpawns: Map<String, MutableSet<GearyEntity>> = mapOf()
    val spawnConfigsQuery = geary.cachedQuery(SpawnConfigs())

    /** Clears [regionSpawns] */
    fun unregisterSpawns() { regionSpawns = mapOf() }

    fun reloadSpawns() {
        unregisterSpawns()
        spawnConfigsQuery.entities().forEach {
            runCatching {
                prefabLoader.reload(it)
            }.onFailure {
                it.printStackTrace()
            }
        }
        loadSpawns()
    }

    fun loadSpawns() {
        val map = mutableMapOf<String, MutableSet<GearyEntity>>()
        spawnsWithWGRegion.mapWithEntity {
            parentRegions.keys
        }.forEach {
            it.data.forEach { regionName ->
                map.getOrPut(regionName) { mutableSetOf() }.add(it.entity)
            }
        }
        regionSpawns = map

    }

    /** Takes a list of spawn region names and converts to a list of [SpawnDefinition]s from those regions */
    fun List<ProtectedRegion>.getMobSpawnsForRegions(): List<GearyEntity> =
        flatMap { regionSpawns[it.id] ?: setOf() }


    class SpawnConfigs : GearyQuery() {
        val config by get<SpawnType>()
    }
}
