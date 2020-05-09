package com.mineinabyss.mobzy.configuration

import com.mineinabyss.mobzy.mobs.MobTemplate
import kotlinx.serialization.Serializable
import org.bukkit.plugin.Plugin
import java.io.File

class MobConfiguration(
        file: File,
        plugin: Plugin
) : SerializableConfiguration<MobConfiguration.MobCfgInfo>(file, plugin, MobCfgInfo.serializer()) {
//    override val serialFormat = Yaml(configuration = YamlConfiguration(encodeDefaults = false))

    @Serializable
    data class MobCfgInfo(
            val templates: Map<String, MobTemplate>
    )
}