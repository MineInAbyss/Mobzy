package com.mineinabyss.mobzy.modelengine.animation

import com.mineinabyss.mobzy.modelengine.toModelEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class ModelInteractionListener: Listener {
    /** Switch to the hit model of the entity, then shortly after, back to the normal one to create a hit effect. */
    @EventHandler(ignoreCancelled = true)
    fun EntityDamageEvent.onHit() {
        entity.toModelEntity()?.mountManager?.model?.hurt()
    }
}
