package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.papermc.access.toBukkit
import com.mineinabyss.geary.papermc.events.GearyMinecraftSpawnEvent
import com.mineinabyss.mobzy.ecs.components.RemoveWhenFarAway
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object MobzyECSListener : Listener {
    @EventHandler
    fun GearyMinecraftSpawnEvent.setRemoveWhenFarAway() {
        val bukkit = entity.toBukkit<LivingEntity>() ?: return
        bukkit.removeWhenFarAway = entity.has<RemoveWhenFarAway>()
    }
}
