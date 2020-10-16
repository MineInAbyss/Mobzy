package com.mineinabyss.looty.ecs.config

import com.mineinabyss.geary.ecs.types.GearyEntityTypes
import org.bukkit.inventory.ItemStack

object LootyTypes : GearyEntityTypes<LootyType>() {
    operator fun get(item: ItemStack): LootyType = TODO("Getting type from item not yet implemented")
}