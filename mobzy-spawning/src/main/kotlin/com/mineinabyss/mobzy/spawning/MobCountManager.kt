package com.mineinabyss.mobzy.spawning

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.api.annotations.Handler
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.geary.papermc.access.toGearyOrNull
import com.mineinabyss.mobzy.MobzyConfig
import com.mineinabyss.mobzy.ecs.components.MobCategory
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.concurrent.atomic.AtomicInteger

context(GearyMCContext)
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
        val TargetScope.bukkitEntity by added<MobCategory>()
        val TargetScope.category by added<MobCategory>()

        @Handler
        fun TargetScope.count() {
            categoryCounts.getOrPut(category) { AtomicInteger() }.getAndIncrement()
        }
    }
}
