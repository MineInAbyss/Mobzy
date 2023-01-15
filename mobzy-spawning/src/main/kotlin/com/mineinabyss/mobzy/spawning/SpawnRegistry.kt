package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.prefabs.prefabs
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.geary.systems.query.GearyQuery
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
    val spawnConfigsQuery = SpawnConfigs()

    /** Clears [regionSpawns] */
    fun unregisterSpawns() = regionSpawns.clear()

    fun reloadSpawns() {
        unregisterSpawns()
        spawnConfigsQuery.toList().forEach {
            prefabLoader.reread(it.entity)
        }
    }

    /** Takes a list of spawn region names and converts to a list of [SpawnDefinition]s from those regions */
    fun List<ProtectedRegion>.getMobSpawnsForRegions(): List<GearyEntity> =
        flatMap { regionSpawns[it.id] ?: setOf() }

    inner class SpawnTracker : GearyListener() {
        private val TargetScope.parentRegions by onSet<WGRegions>()
        private val TargetScope.spawn by onSet<SpawnType>()

        @Handler
        fun TargetScope.trackSpawns() {
            parentRegions.keys.forEach {
                regionSpawns.getOrPut(it) { mutableSetOf() } += entity
            }
        }
    }

    class SpawnConfigs : GearyQuery() {
        val TargetScope.config by get<SpawnType>()
    }
}
