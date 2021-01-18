package com.mineinabyss.mobzy.ecs.events

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import com.mineinabyss.geary.ecs.components.Target
import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.minecraft.events.event
import com.mineinabyss.geary.minecraft.store.geary
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

object MobzyEventListener : Listener {
    @EventHandler
    fun ProjectileCollideEvent.onCollision() {
        //TODO: When do we remove this component?
        geary(entity)?.addComponent(Target(geary(collidedWith) ?: return))

        event(geary(entity), "projectileHit")
    }


    @EventHandler
    fun EntityDamageByEntityEvent.onDamage() {
        //TODO: When do we remove this component?
        geary(damager)?.addComponent(Target(geary(entity) ?: return))

        event(geary(damager), "onTargetHit")
    }
}