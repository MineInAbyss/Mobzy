package com.mineinabyss.mobzy.ecs.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called whenever an entity first gets spawned, after components are attached.
 *
 * Use this to modify persistent things on the entity, and use [EntityLoadedEvent] for non-persistent things.
 */
data class EntityCreatedEvent(
        val id: Int
): Event() {
    override fun getHandlers() = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}