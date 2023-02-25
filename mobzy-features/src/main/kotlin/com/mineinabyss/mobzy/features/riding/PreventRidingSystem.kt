package com.mineinabyss.mobzy.features.riding

import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleEnterEvent

class PreventRidingSystem: Listener {

    /** Prevents entities with <PreventRiding> component (NPCs) from getting in boats and other vehicles. */
    @EventHandler
    fun VehicleEnterEvent.onVehicleEnter() {
        val gearyEntity = entered.toGearyOrNull() ?: return
        if (gearyEntity.has<PreventRiding>())
            isCancelled = true
    }
}
