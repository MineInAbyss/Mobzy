package com.mineinabyss.mobzy.ecs.events

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

data class PlayerRightClickEntityEvent(
    val player: Player,
    val entity: Entity
) : Event() {
    override fun getHandlers() = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
