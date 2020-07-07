package com.mineinabyss.mobzy.configuration

import com.mineinabyss.mobzy.mobs.MobTemplate
import kotlinx.serialization.Serializable
import org.bukkit.plugin.Plugin
import java.io.File

class MobConfig(
        file: File,
        plugin: Plugin
) : SerializableConfig<MobConfig.MobCfgInfo>(file, plugin, MobCfgInfo.serializer()) {
//    override val serialFormat = Yaml(configuration = YamlConfiguration(encodeDefaults = false))

    @Serializable
    data class MobCfgInfo(
            val templates: Map<String, MobTemplate>
    )
}