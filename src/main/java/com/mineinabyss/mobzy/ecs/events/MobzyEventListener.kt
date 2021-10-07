package com.mineinabyss.mobzy.ecs.events

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.components.Target
import com.mineinabyss.geary.minecraft.access.toGeary
import com.mineinabyss.geary.minecraft.access.toGearyOrNull
import com.mineinabyss.geary.minecraft.components.event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent

object MobzyEventListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun ProjectileCollideEvent.onCollision() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        com.mineinabyss.geary.ecs.api.entities.GearyEntity
        GearyEntity
        gearyEntity.set(Target(collidedWith.toGeary()))

        event(gearyEntity, "projectileHit")

        gearyEntity.remove<Target>()
    }

    @EventHandler(ignoreCancelled = true)
    fun ProjectileHitEvent.onProjectileLand() {
        event(entity.toGearyOrNull(), "projectileLand")
    }

    @EventHandler(ignoreCancelled = true)
    fun EntityDamageByEntityEvent.onDamage() {
        val gearyEntity = damager.toGearyOrNull() ?: return

        gearyEntity.set(Target(entity.toGeary()))

        event(gearyEntity, "onTargetHit")

        gearyEntity.remove<Target>()
    }

    @EventHandler(ignoreCancelled = true)
    fun EntityDamageEvent.onDamaged(){
        event(entity.toGearyOrNull(), "onDamaged")
    }
}
