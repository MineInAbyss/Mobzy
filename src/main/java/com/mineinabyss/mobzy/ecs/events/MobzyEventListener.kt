package com.mineinabyss.mobzy.ecs.events

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import com.mineinabyss.geary.ecs.components.Target
import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.geary.minecraft.access.gearyOrNull
import com.mineinabyss.geary.minecraft.events.event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent

object MobzyEventListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun ProjectileCollideEvent.onCollision() {
        val gearyEntity = gearyOrNull(entity) ?: return

        gearyEntity.set(Target(geary(collidedWith)))

        event(gearyEntity, "projectileHit")

        gearyEntity.remove<Target>()
    }

    @EventHandler(ignoreCancelled = true)
    fun ProjectileHitEvent.onProjectileLand() {
        event(gearyOrNull(entity), "projectileLand")
    }

    @EventHandler(ignoreCancelled = true)
    fun EntityDamageByEntityEvent.onDamage() {
        val gearyEntity = gearyOrNull(damager) ?: return

        gearyEntity.set(Target(geary(entity)))

        event(gearyEntity, "onTargetHit")

        gearyEntity.remove<Target>()
    }
}
