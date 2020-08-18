package com.mineinabyss.mobzy.configuration

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.mineinabyss.mobzy.MobzyAddon
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.registration.MobTypes
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus

object MobTypeConfigs {
    private var module = SerializersModule { }

    @UnstableDefault
    val format by lazy {
        Json(
                context = module,
                configuration = JsonConfiguration(encodeDefaults = false, useArrayPolymorphism = true)
        )
    }

    val formatYaml by lazy { Yaml(context = module, configuration = YamlConfiguration(encodeDefaults = false)) }

    fun addSerializerModule(module: SerialModule) {
        this.module += module
    }

    fun registerTypes(mobzyAddon: MobzyAddon) {
        mobzyAddon.mobConfigDir.walk().filter { it.isFile }.forEach { file ->
            val name = file.nameWithoutExtension
            val type = formatYaml.parse(MobType.serializer(), file.readText())
            MobzyTypeRegistry.registerMob(name, type)
            MobTypes.registerTypes(mapOf(name to type))
        }
    }
}