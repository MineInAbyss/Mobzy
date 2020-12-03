package com.mineinabyss.mobzy.configuration

import com.mineinabyss.idofront.annotations.GenerateConfigExtensions
import com.mineinabyss.idofront.config.IdofrontConfig
import com.mineinabyss.mobzy.spawning.regions.SpawnRegion
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.plugin.Plugin
import java.io.File

@GenerateConfigExtensions
class SpawnConfig(
        file: File,
        plugin: Plugin
) : IdofrontConfig<SpawnConfig.Data>(plugin, Data.serializer(), file) {
    @Serializable
    class Data(
            val name: String,
            val icon: Material,
            val regions: List<SpawnRegion>
    )
}