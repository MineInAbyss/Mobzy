package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.annotations.Handler
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.mobzy.ecs.components.initialization.ItemModel
import org.bukkit.entity.Snowball

@AutoScan
class ApplyItemModelListener : GearyListener() {
    val TargetScope.model by added<ItemModel>()
    val TargetScope.bukkit by added<Snowball>()

    @Handler
    fun TargetScope.applyModel() {
        val modelItem = model.item.toItemStack()
        bukkit.item = modelItem
    }
}
