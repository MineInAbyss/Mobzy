package com.mineinabyss.mobzy.ecs.listeners

import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.minecraft.components.toBukkit
import com.mineinabyss.geary.minecraft.store.with
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.api.pathfindergoals.addTargetSelector
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.Pathfinders
import com.mineinabyss.mobzy.ecs.components.interaction.AttackPotionEffects
import com.mineinabyss.mobzy.ecs.events.MobzyLoadEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import kotlin.random.Random

object MobzyECSListener : Listener {
    @EventHandler
    fun MobzyLoadEvent.attachPathfindersOnEntityLoadedEvent() {
        val mob = entity.toBukkit<Mob>() ?: return
        val (targets, goals) = entity.get<Pathfinders>() ?: return

        targets?.forEach { (priority, component) ->
            mob.toNMS().addTargetSelector(priority.toInt(), component.build(mob))

            entity.addComponent(component)
        }
        goals?.forEach { (priority, component) ->
            mob.toNMS().addPathfinderGoal(priority.toInt(), component.build(mob))
            entity.addComponent(component)
        }
    }

    @EventHandler
    fun EntityDamageByEntityEvent.onMobAttack() {
        val target = (entity as? LivingEntity) ?: return
        damager.with<AttackPotionEffects> { (effects, applyChance) ->
            if (Random.nextDouble() <= applyChance)
                target.addPotionEffects(effects)
        }
    }
}
