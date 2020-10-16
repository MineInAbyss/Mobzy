package com.mineinabyss.looty.ecs.config

import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.mobzy.mobzy
import com.okkero.skedule.schedule


object LootyConfig {
    private val addons = mutableListOf<LootyAddon>()

    fun registerAddon(addon: LootyAddon) = addons.add(addon)

    init {
        //first tick only finishes when all plugins are loaded, which is when we activate addons
        mobzy.schedule {
            waitFor(1)
            activateAddons()
        }
    }

    private fun activateAddons() {
        LootyTypes.reset()
        for (addon in addons) {
            addon.relicsDir.walk().filter { it.isFile }.forEach { file ->
                val name = file.nameWithoutExtension
                val type = Formats.yamlFormat.decodeFromString(LootyType.serializer(), file.readText())
                LootyTypes.registerType(name, type) //TODO namespaces
            }
        }
    }
}