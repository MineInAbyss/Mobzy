package com.mineinabyss.looty.ecs.components

import org.bukkit.inventory.ItemStack

class ItemStackComponent(
        val itemStack: ItemStack,
        val inventorySlot: Int,
)