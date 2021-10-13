package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.ecs.api.entities.with
import com.mineinabyss.geary.minecraft.access.toBukkit
import com.mineinabyss.geary.minecraft.events.GearyMinecraftSpawnEvent
import com.mineinabyss.mobzy.ecs.components.initialization.ItemModel
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object MobzyECSListener : Listener {
    @EventHandler
    fun GearyMinecraftSpawnEvent.setItemModel() {
        entity.with { (model): ItemModel ->
            val modelItem = model.toItemStack()
            when (val bukkit = entity.toBukkit()) {
                is Snowball -> bukkit.item = modelItem
            }
        }
    }

    @EventHandler
    fun GearyMinecraftSpawnEvent.setRemoveWhenFarAway() {
        val bukkit = entity.toBukkit<LivingEntity>() ?: return
        bukkit.removeWhenFarAway = entity.has<com.mineinabyss.mobzy.ecs.components.RemoveWhenFarAway>()
    }
}
