package com.mineinabyss.mobzy.configuration

import com.mineinabyss.mobzy.spawning.regions.SpawnRegion
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.plugin.Plugin
import java.io.File

class SpawnConfiguration(
        file: File,
        plugin: Plugin
) : SerializableConfiguration<SpawnConfiguration.SpawnCfgInfo>(file, plugin, SpawnCfgInfo.serializer()) {
    @Serializable
    data class SpawnCfgInfo(
            val name: String,
            val icon: Material,
            val regions: List<SpawnRegion>
    )
}