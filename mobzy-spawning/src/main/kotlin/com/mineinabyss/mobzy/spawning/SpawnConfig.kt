package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.mobzy.spawning.regions.SpawnRegion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
@SerialName("mobzy:spawns")
@AutoscanComponent
class SpawnConfig(
    val name: String,
    val icon: Material,
    val regions: List<SpawnRegion>
)
