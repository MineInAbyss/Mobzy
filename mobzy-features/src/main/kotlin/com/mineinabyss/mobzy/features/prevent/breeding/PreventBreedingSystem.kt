package com.mineinabyss.mobzy.features.prevent.breeding

import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityBreedEvent
import org.bukkit.event.entity.EntityEnterLoveModeEvent

class PreventBreedingSystem : Listener {
    @EventHandler
    fun EntityEnterLoveModeEvent.cancelLove() {
        if (entity.toGearyOrNull()?.has<PreventBreeding>() == true) isCancelled = true
    }

    @EventHandler
    fun EntityBreedEvent.cancelBreed() {
        if (entity.toGearyOrNull()?.has<PreventBreeding>() == true) isCancelled = true
    }
}
