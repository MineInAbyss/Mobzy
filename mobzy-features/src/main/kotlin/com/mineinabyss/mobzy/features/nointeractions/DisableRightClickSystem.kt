package com.mineinabyss.mobzy.features.nointeractions

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

@AutoScan
class DisableRightClickSystem : Listener {
    @EventHandler
    fun PlayerInteractEntityEvent.cancelRightClick() {
        if (rightClicked.toGearyOrNull()?.has<DisableRightClick>() == true) {
            isCancelled = true
        }
    }
}
