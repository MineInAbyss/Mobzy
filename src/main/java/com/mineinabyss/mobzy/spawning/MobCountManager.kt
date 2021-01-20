package com.mineinabyss.mobzy.spawning

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.minecraft.store.gearyOrNull
import com.mineinabyss.mobzy.ecs.components.initialization.MobCategory

object MobCountManager {
    val entities: MutableMap<MobCategories, MutableSet<GearyEntity>> = mutableMapOf()

    fun EntityAddToWorldEvent.registerOnAdd() {
        val gearyEntity = gearyOrNull(entity) ?: return
        val (category) = gearyEntity.get<MobCategory>() ?: return
        entities.getOrPut(category) { mutableSetOf() }.add(gearyEntity)
    }

    fun EntityRemoveFromWorldEvent.unregisterOnRemove() {
        val gearyEntity = gearyOrNull(entity) ?: return
        val (category) = gearyEntity.get<MobCategory>() ?: return
        entities[category]?.remove(gearyEntity)
    }
}
