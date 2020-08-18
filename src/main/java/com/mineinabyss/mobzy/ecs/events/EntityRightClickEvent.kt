package com.mineinabyss.mobzy.ecs.events

import com.mineinabyss.mobzy.mobs.CustomMob
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

data class EntityRightClickEvent(
        val mob: CustomMob
): Event() {
    override fun getHandlers() = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}