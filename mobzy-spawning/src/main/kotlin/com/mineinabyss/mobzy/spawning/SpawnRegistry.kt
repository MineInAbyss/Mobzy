package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.annotations.optin.UnsafeAccessors
import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.prefabs.prefabs
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointer
import com.mineinabyss.geary.systems.accessors.Pointers
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
        spawnConfigsQuery.matchedEntities.forEach {
            prefabLoader.reload(it)
        }
    }

    /** Takes a list of spawn region names and converts to a list of [SpawnDefinition]s from those regions */
    fun List<ProtectedRegion>.getMobSpawnsForRegions(): List<GearyEntity> =
        flatMap { regionSpawns[it.id] ?: setOf() }

    inner class SpawnTracker : GearyListener() {
        private val Pointers.parentRegions by get<WGRegions>().whenSetOnTarget()
        private val Pointers.spawn by get<SpawnType>().whenSetOnTarget()

        @OptIn(UnsafeAccessors::class)
        override fun Pointers.handle() {
            parentRegions.keys.forEach {
                regionSpawns.getOrPut(it) { mutableSetOf() } += target.entity
            }
        }
    }

    class SpawnConfigs : GearyQuery() {
        val Pointer.config by get<SpawnType>()
    }
}
