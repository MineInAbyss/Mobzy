package com.mineinabyss.mobzy.features.prevent.interaction

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

@AutoScan
class PreventInteractionSystem : Listener {
    @EventHandler
    fun PlayerInteractEntityEvent.onPlayerRightClick() {
        val preventInteraction = rightClicked.toGearyOrNull()?.get<PreventInteraction>() ?: return
        if (preventInteraction.type.isEmpty() || preventInteraction.type.contains(InteractionType.RIGHT_CLICK)) {
            isCancelled = true
        }
    }

    @EventHandler
    fun EntityDamageByEntityEvent.onPlayerDamage() {
        if (damager !is Player) return
        val preventInteraction = entity.toGearyOrNull()?.get<PreventInteraction>() ?: return
        if (preventInteraction.type.isEmpty() || preventInteraction.type.contains(InteractionType.ATTACK)) {
            isCancelled = true
        }
    }
}
