package com.mineinabyss.mobzy.spawning

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.geary.papermc.GearyMCContextKoin
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.mobzy.MobzyConfig
import com.mineinabyss.mobzy.ecs.components.MobCategory
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.koin.core.component.inject
import java.util.concurrent.atomic.AtomicInteger

object MobCountManager : Listener, GearyMCContext by GearyMCContextKoin() {
    val categoryCounts: MutableMap<MobCategory, AtomicInteger> = mutableMapOf()
    val config by inject<MobzyConfig>()

    fun isCategoryAllowed(category: MobCategory) =
        (categoryCounts[category]?.get() ?: 0) <=
                (config.getCreatureTypeCap(category) * GlobalSpawnInfo.playerGroupCount)

    @EventHandler
    fun EntityRemoveFromWorldEvent.unregisterOnRemove() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val category = gearyEntity.get<MobCategory>() ?: return
        categoryCounts[category]?.getAndDecrement()
    }

    @AutoScan
    class CountMobsSystem : GearyListener() {
        val TargetScope.bukkitEntity by onSet<MobCategory>()
        val TargetScope.category by onSet<MobCategory>()

        @Handler
        fun TargetScope.count() {
            categoryCounts.getOrPut(category) { AtomicInteger() }.getAndIncrement()
        }
    }
}
