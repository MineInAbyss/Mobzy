package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.mobzy.ecs.BukkitEntityAccess
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerJoinLeaveListener : Listener {
    @EventHandler
    fun onPlayerLogin(e: PlayerLoginEvent) {
        val (player) = e
        BukkitEntityAccess.registerPlayer(player)
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val (player) = e
        BukkitEntityAccess.unregisterPlayer(player)
    }
}
