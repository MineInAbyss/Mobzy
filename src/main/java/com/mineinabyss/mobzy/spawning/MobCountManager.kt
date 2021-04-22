package com.mineinabyss.mobzy.spawning

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.minecraft.access.gearyOrNull
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.concurrent.atomic.AtomicInteger

object MobCountManager: Listener {
    val entities: MutableMap<MobCategory, AtomicInteger> = mutableMapOf()

    @EventHandler
    fun EntityAddToWorldEvent.registerOnAdd() {
        val gearyEntity = gearyOrNull(entity) ?: return
        val category = gearyEntity.get<MobCategory>() ?: return
        entities.getOrPut(category) { AtomicInteger() }.getAndIncrement()
    }

    @EventHandler
    fun EntityRemoveFromWorldEvent.unregisterOnRemove() {
        val gearyEntity = gearyOrNull(entity) ?: return
        val category = gearyEntity.get<MobCategory>() ?: return
        entities[category]?.getAndDecrement()
    }
}
