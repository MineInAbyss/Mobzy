package com.mineinabyss.mobzy.ecs.events

import com.mineinabyss.geary.ecs.GearyEntity
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called whenever an entity first gets spawned, after components are attached.
 *
 * Use this to modify persistent things on the entity, and use [MobLoadEvent] for non-persistent things.
 */
data class MobSpawnEvent(
        val entity: GearyEntity
): Event() {
    override fun getHandlers() = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}