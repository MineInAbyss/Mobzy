package com.mineinabyss.mobzy.ecs.events

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import com.mineinabyss.geary.ecs.components.Target
import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.ecs.components.removeComponent
import com.mineinabyss.geary.minecraft.events.event
import com.mineinabyss.geary.minecraft.store.geary
import com.mineinabyss.geary.minecraft.store.gearyOrNull
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent

object MobzyEventListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun ProjectileCollideEvent.onCollision() {
        val gearyEntity = gearyOrNull(entity) ?: return

        gearyEntity.addComponent(Target(geary(collidedWith)))

        event(gearyEntity, "projectileHit")

        gearyEntity.removeComponent<Target>()
    }

    @EventHandler(ignoreCancelled = true)
    fun ProjectileHitEvent.onProjectileLand(){
        event(gearyOrNull(entity), "projectileLand")
    }

    @EventHandler(ignoreCancelled = true)
    fun EntityDamageByEntityEvent.onDamage() {
        val gearyEntity = gearyOrNull(damager) ?: return

        gearyEntity.addComponent(Target(geary(entity)))

        event(gearyEntity, "onTargetHit")

        gearyEntity.removeComponent<Target>()
    }
}
