package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.geary.prefabs.prefabs
import com.mineinabyss.geary.systems.builders.cachedQuery
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.GearyQuery
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedRegion

/**
 * A singleton for keeping track of registered [SpawnRegion]s. Used for the mob spawning system
 *
 * @property regionSpawns A map of region names to their [SpawnRegion].
 */
class SpawnRegistry {
    private val prefabLoader get() = prefabs.loader

    private val regionContainer = WorldGuard.getInstance().platform.regionContainer
    private val regionSpawns: MutableMap<String, MutableSet<GearyEntity>> = HashMap()
    val spawnConfigsQuery = geary.cachedQuery(SpawnConfigs())

    /** Clears [regionSpawns] */
    fun unregisterSpawns() = regionSpawns.clear()

    fun reloadSpawns() {
        unregisterSpawns()
        spawnConfigsQuery.entities().forEach {
            prefabLoader.reload(it)
        }
    }

    /** Takes a list of spawn region names and converts to a list of [SpawnDefinition]s from those regions */
    fun List<ProtectedRegion>.getMobSpawnsForRegions(): List<GearyEntity> =
        flatMap { regionSpawns[it.id] ?: setOf() }

    val spawnTracker = geary.listener(object : ListenerQuery() {
        val parentRegions by get<WGRegions>()
        val spawn by get<SpawnType>()
        override fun ensure() = event.anySet(::parentRegions, ::spawn)
    }).exec {
        parentRegions.keys.forEach {
            regionSpawns.getOrPut(it) { mutableSetOf() } += entity
        }
    }

    class SpawnConfigs : GearyQuery() {
        val config by get<SpawnType>()
    }
}
