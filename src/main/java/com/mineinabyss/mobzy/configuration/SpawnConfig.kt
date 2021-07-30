package com.mineinabyss.mobzy.configuration

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.idofront.config.IdofrontConfig
import com.mineinabyss.mobzy.spawning.regions.SpawnRegion
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.plugin.Plugin
import java.io.File

class SpawnConfig(
    file: File,
    plugin: Plugin
) : IdofrontConfig<SpawnConfig.Data>(
    plugin, Data.serializer(), file,
    format = Formats.yamlFormat
) {
    @Serializable
    class Data(
        val name: String,
        val icon: Material,
        val regions: List<SpawnRegion>
    )
}
