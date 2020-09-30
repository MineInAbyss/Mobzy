package com.mineinabyss.looty.ecs.config

import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.registration.MobTypes
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry


object LootyConfig {
    private val addons = mutableListOf<LootyAddon>()

    fun registerAddon(addon: LootyAddon) = addons.add(addon)

    fun reload() {
        for (addon in addons) {
            addon.relicsDir.walk().filter { it.isFile }.forEach { file ->
                val name = file.nameWithoutExtension
                val type = Formats.yamlFormat.decodeFromString(MobType.serializer(), file.readText())
                MobzyTypeRegistry.registerMob(name, type)
                MobTypes.registerTypes(mapOf(name to type))
            }
        }
    }
}