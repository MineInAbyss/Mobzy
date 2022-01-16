package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.autoscan.Handler
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.mobzy.ecs.components.initialization.ItemModel
import org.bukkit.entity.Snowball

@AutoScan
class ApplyItemModelListener : GearyListener() {
    val TargetScope.model by get<ItemModel>()
    val TargetScope.bukkit by get<Snowball>()

    init {
        allAdded()
    }

    @Handler
    fun TargetScope.applyModel() {
        val modelItem = model.item.toItemStack()
        bukkit.item = modelItem
    }
}
