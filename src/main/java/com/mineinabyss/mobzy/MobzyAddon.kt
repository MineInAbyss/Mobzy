package com.mineinabyss.mobzy

import org.bukkit.plugin.Plugin
import java.io.File

/**
 * Allows a plugin to interact and register its own entities with Mobzy
 */
interface MobzyAddon : Plugin {
    val mobConfigDir: File
    val spawnConfig: File
}