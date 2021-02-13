package com.mineinabyss.mobzy.ecs.events

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import com.mineinabyss.geary.ecs.components.Target
import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.ecs.components.removeComponent
import com.mineinabyss.geary.minecraft.events.event
import com.mineinabyss.geary.minecraft.store.geary
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

object MobzyEventListener : Listener {
    @EventHandler
    fun ProjectileCollideEvent.onCollision() {
        geary(entity).addComponent(Target(geary(collidedWith)))

        event(geary(entity), "projectileHit")

        geary(entity).removeComponent<Target>()
    }


    @EventHandler
    fun EntityDamageByEntityEvent.onDamage() {
        geary(damager).addComponent(Target(geary(entity)))

        event(geary(damager), "onTargetHit")

        geary(damager).removeComponent<Target>()
    }
}