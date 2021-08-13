package com.mineinabyss.mobzy.spawning

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.mineinabyss.geary.minecraft.access.gearyOrNull
import com.mineinabyss.geary.minecraft.events.GearyMinecraftLoadEvent
import com.mineinabyss.mobzy.MobzyConfig
import com.mineinabyss.mobzy.mobzyConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.concurrent.atomic.AtomicInteger

object MobCountManager : Listener {
    val categoryCounts: MutableMap<MobCategory, AtomicInteger> = mutableMapOf()

    fun isCategoryAllowed(category: MobCategory) =
        categoryCounts[category]?.get() ?: 0 <= MobzyConfig.getCreatureTypeCap(category) * GlobalSpawnInfo.playerGroupCount

    @EventHandler
    fun GearyMinecraftLoadEvent.registerOnAdd() {
        val category = entity.get<MobCategory>() ?: return
        categoryCounts.getOrPut(category) { AtomicInteger() }.getAndIncrement()
    }

    @EventHandler
    fun EntityRemoveFromWorldEvent.unregisterOnRemove() {
        val gearyEntity = gearyOrNull(entity) ?: return
        val category = gearyEntity.get<MobCategory>() ?: return
        categoryCounts[category]?.getAndDecrement()
    }
}
