package com.mineinabyss.mobzy.spawning.regions

import com.mineinabyss.mobzy.api.nms.entity.typeName
import com.mineinabyss.mobzy.spawning.MobSpawn
import com.mineinabyss.mobzy.spawning.SpawnRegistry
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R1.EntityTypes
import org.bukkit.Material

/**
 * A region with determined hostile, passive, flying, etc... spawns.
 */
@Serializable
class SpawnRegion(
        val name: String,
        val icon: Material = Material.BEDROCK,
        val spawns: List<MobSpawn> = listOf()
) {
    init {
        SpawnRegistry += this
    }

    fun getSpawnOfType(type: EntityTypes<*>): MobSpawn = spawns
            .firstOrNull { it.entityType == type }
            ?: error("Could not find ${type.typeName} from ${spawns.map { it.entityTypeName }}")
}