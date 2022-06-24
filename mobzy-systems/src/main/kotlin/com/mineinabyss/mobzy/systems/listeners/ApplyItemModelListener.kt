package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.mobzy.ecs.components.initialization.ItemModel
import org.bukkit.entity.Snowball

@AutoScan
class ApplyItemModelListener : GearyListener() {
    val TargetScope.model by onSet<ItemModel>()
    val TargetScope.bukkit by onSet<Snowball>()

    @Handler
    fun TargetScope.applyModel() {
        val modelItem = model.item.toItemStack()
        bukkit.item = modelItem
    }
}
