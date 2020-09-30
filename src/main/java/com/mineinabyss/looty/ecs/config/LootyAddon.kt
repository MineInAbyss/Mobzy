package com.mineinabyss.looty.ecs.config

import org.bukkit.plugin.Plugin
import java.io.File

/**
 * Allows a plugin to interact and register its own entities with Looty
 */
interface LootyAddon : Plugin {
    val relicsDir: File
}

fun LootyAddon.registerAddonWithLooty(){
    LootyConfig.registerAddon(this)
}