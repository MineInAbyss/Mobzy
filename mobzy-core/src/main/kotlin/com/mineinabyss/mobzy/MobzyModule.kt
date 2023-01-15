package com.mineinabyss.mobzy

import com.mineinabyss.idofront.di.DI
import org.bukkit.plugin.java.JavaPlugin

val mobzy by DI.observe<MobzyModule>()

interface MobzyModule {
    val plugin: JavaPlugin
    val config: MobzyConfig
}
