package com.mineinabyss.mobzy.ecs.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called whenever an entity gets loaded after components are attached. For instance, when a chunk gets loaded with this
 * entity inside.
 *
 * Unlike [EntityCreatedEvent] this keeps getting called upon subsequent loads of the entity, not just the first
 * creation. As such, use this event for doing non-persistent things with the Entity.
 */
data class EntityLoadedEvent(
        val id: Int
): Event() {
    override fun getHandlers() = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}