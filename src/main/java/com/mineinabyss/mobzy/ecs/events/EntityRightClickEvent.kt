package com.mineinabyss.mobzy.ecs.events

import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

data class EntityRightClickEvent(
    val player: Player,
    val mob: Mob
) : Event() {
    override fun getHandlers() = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
