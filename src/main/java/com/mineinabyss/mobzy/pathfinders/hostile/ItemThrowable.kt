package com.mineinabyss.mobzy.pathfinders.hostile

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

interface ItemThrowable {
    val itemToThrow: ItemStack

    fun throwItem(target: LivingEntity)
}