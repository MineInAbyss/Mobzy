package com.mineinabyss.geary.ecs.components

import com.mineinabyss.geary.ecs.MobzyComponent
import org.bukkit.Bukkit

abstract class CooldownComponent : MobzyComponent {
    abstract val ticks: Int
    private var cooldownStart: Int = 0
    val cooledDown get() = System.currentTimeMillis() - ticks
    fun cooledDown(currentTime: Int = Bukkit.getServer().currentTick) = cooldownStart < currentTime - ticks
    fun restartCooldown() {
        cooldownStart = Bukkit.getServer().currentTick
    }
}