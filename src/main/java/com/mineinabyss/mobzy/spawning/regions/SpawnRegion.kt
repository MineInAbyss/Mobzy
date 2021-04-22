package com.mineinabyss.mobzy.spawning.regions

import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.mobzy.spawning.SpawnDefinition
import com.mineinabyss.mobzy.spawning.SpawnRegistry
import kotlinx.serialization.Serializable
import org.bukkit.Material

/**
 * A region with determined hostile, passive, flying, etc... spawns.
 */
@Serializable
class SpawnRegion(
    val name: String,
    val icon: Material = Material.BEDROCK,
    val spawns: List<SpawnDefinition> = listOf()
) {
    init {
        SpawnRegistry += this
    }

    fun getSpawnOfType(type: PrefabKey): SpawnDefinition = spawns
        .firstOrNull { it.prefabKey == type }
        ?: error("Could not find $type from ${spawns.map { it.prefab }}")
}
