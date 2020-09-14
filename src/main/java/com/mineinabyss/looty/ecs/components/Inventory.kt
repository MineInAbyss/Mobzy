package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.MobzyComponent
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bukkit.inventory.ItemStack

@Serializable
class Inventory : MobzyComponent() {
    @Transient
    internal var itemCache = mutableMapOf<Int, Pair<ItemStack, GearyEntity>>()

    operator fun component1() = itemCache

    fun entityAt(slot: Int): GearyEntity? = itemCache[slot]?.second
}