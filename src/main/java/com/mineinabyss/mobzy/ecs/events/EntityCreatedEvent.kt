package com.mineinabyss.mobzy.ecs.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

data class EntityCreatedEvent(
        val id: Int
): Event() {
    override fun getHandlers() = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}