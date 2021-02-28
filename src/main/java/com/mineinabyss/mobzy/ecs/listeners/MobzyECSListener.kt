package com.mineinabyss.mobzy.ecs.listeners

import com.mineinabyss.geary.minecraft.access.toBukkit
import com.mineinabyss.geary.minecraft.events.GearyMinecraftLoadEvent
import com.mineinabyss.geary.minecraft.events.GearyMinecraftSpawnEvent
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.api.pathfindergoals.addTargetSelector
import com.mineinabyss.mobzy.ecs.components.initialization.ItemModel
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.Pathfinders
import org.bukkit.entity.Mob
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object MobzyECSListener : Listener {
    @EventHandler
    fun GearyMinecraftLoadEvent.attachPathfindersOnEntityLoadedEvent() {
        val mob = entity.toBukkit<Mob>() ?: return
        val (targets, goals) = entity.get<Pathfinders>() ?: return

        targets?.forEach { (priority, component) ->
            mob.toNMS().addTargetSelector(priority.toInt(), component.build(mob))

            entity.set(component)
        }
        goals?.forEach { (priority, component) ->
            mob.toNMS().addPathfinderGoal(priority.toInt(), component.build(mob))
            entity.set(component)
        }
    }

    @EventHandler
    fun GearyMinecraftSpawnEvent.setItemModel() {
        entity.with<ItemModel> { (model) ->
            val modelItem = model.toItemStack()
            when (val bukkit = entity.toBukkit()) {
                is Snowball -> bukkit.item = modelItem
            }
        }
    }
}
