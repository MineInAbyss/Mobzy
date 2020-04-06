package com.mineinabyss.mobzy

import com.mineinabyss.mobzy.registration.MobzyTemplates
import org.bukkit.plugin.Plugin
import java.io.File

/**
 * Allows a plugin to interact and register its own entities with Mobzy
 */
interface MobzyAddon : Plugin {
    val mobConfig: File
    val spawnConfig: File
    val initializeMobs: () -> Any

    fun registerWithMobzy() {
        MobzyConfig.registerMobCfg(mobConfig, this)
        initializeMobs()
        MobzyTemplates.loadTemplatesFromConfig()//TODO only load templates from this addon's config, don't reload all of them!
        MobzyConfig.registerSpawnCfg(spawnConfig, this)
        mobzy.reloadExistingEntities()
    }
}