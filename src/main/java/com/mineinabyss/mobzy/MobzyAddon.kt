package com.mineinabyss.mobzy

import org.bukkit.plugin.Plugin
import java.io.File

/**
 * Allows a plugin to interact and register its own entities with Mobzy
 */
interface MobzyAddon : Plugin {
    val mobConfig: File
    val spawnConfig: File
    val initializeMobs: () -> Unit

    fun registerWithMobzy() {
        registerMobConfig(mobConfig, this)
        initializeMobs()
        registerSpawnConfig(spawnConfig, this)
        mobzy.reloadExistingEntities()
    }
}