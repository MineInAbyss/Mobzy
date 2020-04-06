package com.mineinabyss.mobzy.spawning.regions

import com.mineinabyss.mobzy.api.creatureType
import com.mineinabyss.mobzy.api.typeName
import com.mineinabyss.mobzy.spawning.MobSpawn
import net.minecraft.server.v1_15_R1.EntityTypes
import java.util.*

/**
 * A region with determined hostile, passive, flying, etc... spawns.
 */
class SpawnRegion(val name: String, vararg spawns: MobSpawn) {
    //TODO maybe mob caps should be determined per region?
    private val spawns: MutableMap<String, MutableList<MobSpawn>> = HashMap()

    fun getSpawnsFor(creatureType: String): List<MobSpawn> {
        return (spawns[creatureType] ?: return emptyList()).toList()
    }

    fun addSpawn(spawn: MobSpawn) {
        //add to a different spawn list depending on what kind of entity type it is (since we have separate mob caps per list)
        val creatureType = spawn.entityType.creatureType.name
        if (spawns[creatureType] == null) spawns[creatureType] = mutableListOf(spawn)
        else spawns[creatureType]!!.add(spawn)
    }

    fun getSpawnOfType(type: EntityTypes<*>): MobSpawn = spawns.values
            .flatten()
            .firstOrNull { (entityType) -> entityType == type }
            ?: error("Could not find ${type.typeName} in ${spawns.values.flatten().map { it.entityType.typeName }}")

    init {
        for (spawn in spawns) addSpawn(spawn)
    }
}