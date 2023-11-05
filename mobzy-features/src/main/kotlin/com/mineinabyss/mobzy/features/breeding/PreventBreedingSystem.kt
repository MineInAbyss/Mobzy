package com.mineinabyss.mobzy.features.breeding

import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityBreedEvent
import org.bukkit.event.entity.EntityEnterLoveModeEvent

class PreventBreedingSystem : Listener {
    @EventHandler
    fun EntityEnterLoveModeEvent.cancelLove() {
        if (entity.toGeary().has<PreventBreeding>()) isCancelled = true
    }

    @EventHandler
    fun EntityBreedEvent.cancelBreed() {
        if (entity.toGeary().has<PreventBreeding>()) isCancelled = true
    }
}
