package com.mineinabyss.mobzy.configuration

import com.mineinabyss.idofront.annotations.GenerateConfigExtensions
import com.mineinabyss.idofront.config.IdofrontConfig
import com.mineinabyss.mobzy.mobs.MobTemplate
import kotlinx.serialization.Serializable
import org.bukkit.plugin.Plugin
import java.io.File

@GenerateConfigExtensions
class MobConfig(
        file: File,
        plugin: Plugin
) : IdofrontConfig<MobConfig.Data>(plugin, Data.serializer(), file) {
    @Serializable
    class Data(
            val templates: Map<String, MobTemplate>
    )
}