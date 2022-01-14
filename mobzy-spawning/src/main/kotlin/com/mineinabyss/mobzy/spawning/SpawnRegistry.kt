package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.autoscan.Handler
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.query.Query
import com.mineinabyss.geary.prefabs.PrefabManager
import com.mineinabyss.mobzy.spawning.SpawnRegistry.regionSpawns
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedRegion

/**
 * A singleton for keeping track of registered [SpawnRegion]s. Used for the mob spawning system
 *
 * @property regionSpawns A map of region names to their [SpawnRegion].
 */
@AutoScan
object SpawnRegistry : GearyListener() {
    private val TargetScope.parentRegions by get<WGRegions>()
    private val TargetScope.spawn by get<SpawnType>()

    private val regionContainer = WorldGuard.getInstance().platform.regionContainer
    private val regionSpawns: MutableMap<String, MutableSet<GearyEntity>> = HashMap()

    init {
        allAdded()
    }

    @Handler
    fun TargetScope.trackSpawns() {
        parentRegions.keys.forEach {
            regionSpawns.getOrPut(it) { mutableSetOf() } += entity
        }
    }


    /** Clears [regionSpawns] */
    fun unregisterSpawns() = regionSpawns.clear()

    object SpawnConfigs : Query() {
        val TargetScope.config by get<SpawnType>()
    }

    fun reloadSpawns() {
        unregisterSpawns()
        SpawnConfigs.toList().forEach {
            PrefabManager.reread(it.entity)
        }
    }

    /** Takes a list of spawn region names and converts to a list of [SpawnDefinition]s from those regions */
    fun List<ProtectedRegion>.getMobSpawnsForRegions(): List<GearyEntity> =
        flatMap { regionSpawns[it.id] ?: setOf() }
}
