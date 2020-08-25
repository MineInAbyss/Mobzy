package com.mineinabyss.mobzy.configuration

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.mineinabyss.mobzy.MobzyAddon
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.registration.MobTypes
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus

object MobTypeConfigs {
    private var module = EmptySerializersModule

    val format by lazy {
        Json {
            serializersModule = module
            useArrayPolymorphism = true
            encodeDefaults = false
        }
    }

    val yamlFormat by lazy {
        Yaml(serializersModule = module, configuration = YamlConfiguration(encodeDefaults = false))
    }

    fun addSerializerModule(module: SerializersModule) {
        this.module += module
    }

    fun registerTypes(mobzyAddon: MobzyAddon) {
        mobzyAddon.mobConfigDir.walk().filter { it.isFile }.forEach { file ->
            val name = file.nameWithoutExtension
            val type = yamlFormat.decodeFromString(MobType.serializer(), file.readText())
            MobzyTypeRegistry.registerMob(name, type)
            MobTypes.registerTypes(mapOf(name to type))
        }
    }
}