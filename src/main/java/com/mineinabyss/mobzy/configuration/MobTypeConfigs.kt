package com.mineinabyss.mobzy.configuration

import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.mobzy.MobzyAddon
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.registration.MobTypes
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry

object MobTypeConfigs {
    fun registerTypes(mobzyAddon: MobzyAddon) {
        mobzyAddon.mobConfigDir.walk().filter { it.isFile }.forEach { file ->
            val name = file.nameWithoutExtension
            val type = Formats.yamlFormat.decodeFromString(MobType.serializer(), file.readText())
            MobzyTypeRegistry.registerMob(name, type)
            MobTypes.registerTypes(mapOf(name to type))
        }
    }
}