package com.mineinabyss.mobzy.spawning

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.mineinabyss.geary.ecs.accessors.EventResultScope
import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.events.handlers.ComponentAddHandler
import com.mineinabyss.geary.minecraft.access.toGearyOrNull
import com.mineinabyss.mobzy.MobzyConfig
import com.mineinabyss.mobzy.ecs.components.MobCategory
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.concurrent.atomic.AtomicInteger

object MobCountManager : Listener {
    val categoryCounts: MutableMap<MobCategory, AtomicInteger> = mutableMapOf()

    fun isCategoryAllowed(category: MobCategory) =
        (categoryCounts[category]?.get() ?: 0) <=
                (MobzyConfig.getCreatureTypeCap(category) * GlobalSpawnInfo.playerGroupCount)

    @EventHandler
    fun EntityRemoveFromWorldEvent.unregisterOnRemove() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val category = gearyEntity.get<MobCategory>() ?: return
        categoryCounts[category]?.getAndDecrement()
    }

    @AutoScan
    class CountMobsSystem : GearyListener() {
        val ResultScope.bukkitEntity by get<MobCategory>()
        val ResultScope.category by get<MobCategory>()

        private inner class Count : ComponentAddHandler() {
            override fun ResultScope.handle(event: EventResultScope) {
                categoryCounts.getOrPut(category) { AtomicInteger() }.getAndIncrement()
            }
        }
    }
}
