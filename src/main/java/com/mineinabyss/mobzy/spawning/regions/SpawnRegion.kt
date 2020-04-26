package com.mineinabyss.mobzy.spawning.regions

import com.mineinabyss.mobzy.api.typeName
import com.mineinabyss.mobzy.spawning.MobSpawn
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_15_R1.EntityTypes
import org.bukkit.Material

/**
 * A region with determined hostile, passive, flying, etc... spawns.
 */
@Serializable
class SpawnRegion(
        val name: String,
        val icon: Material = Material.BEDROCK,
        val spawns: List<MobSpawn>
) {
    //TODO maybe mob caps should be determined per region?
//    private val spawns: MutableMap<String, MutableList<MobSpawn>> = HashMap()

//    fun getSpawnsFor(creatureType: String): List<MobSpawn> {
//        return (spawns[creatureType] ?: return emptyList()).toList()
//    }

    fun getSpawnOfType(type: EntityTypes<*>): MobSpawn = spawns/*.values*/
//            .flatten()
            .firstOrNull { it.entityType == type }
            ?: error("Could not find ${type.typeName} from ${spawns.map { it.entityTypeName }}")
}