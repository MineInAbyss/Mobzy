package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.prefabs.PrefabManager
import com.mineinabyss.geary.prefabs.PrefabManagerContext
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.geary.systems.query.GearyQuery
import com.mineinabyss.mobzy.spawning.SpawnRegistry.regionSpawns
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.koin.core.component.inject

/**
 * A singleton for keeping track of registered [SpawnRegion]s. Used for the mob spawning system
 *
 * @property regionSpawns A map of region names to their [SpawnRegion].
 */
@AutoScan
object SpawnRegistry : GearyListener(), PrefabManagerContext {
    override val prefabManager: PrefabManager by inject()

    private val TargetScope.parentRegions by added<WGRegions>()
    private val TargetScope.spawn by added<SpawnType>()

    private val regionContainer = WorldGuard.getInstance().platform.regionContainer
    private val regionSpawns: MutableMap<String, MutableSet<GearyEntity>> = HashMap()

    @Handler
    fun TargetScope.trackSpawns() {
        parentRegions.keys.forEach {
            regionSpawns.getOrPut(it) { mutableSetOf() } += entity
        }
    }


    /** Clears [regionSpawns] */
    fun unregisterSpawns() = regionSpawns.clear()

    object SpawnConfigs : GearyQuery() {
        val TargetScope.config by get<SpawnType>()
    }

    fun reloadSpawns() {
        unregisterSpawns()
        SpawnConfigs.toList().forEach {
            prefabManager.reread(it.entity)
        }
    }

    /** Takes a list of spawn region names and converts to a list of [SpawnDefinition]s from those regions */
    fun List<ProtectedRegion>.getMobSpawnsForRegions(): List<GearyEntity> =
        flatMap { regionSpawns[it.id] ?: setOf() }
}
