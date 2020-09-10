package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.components.addChild
import com.mineinabyss.geary.ecs.components.addComponents
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.looty.ecs.components.Inventory
import com.mineinabyss.mobzy.ecs.store.decodeComponents
import com.mineinabyss.mobzy.ecs.store.encodeComponents
import com.mineinabyss.mobzy.ecs.store.isGearyEntity
import org.bukkit.inventory.ItemStack

/**
 * ItemStack instances are super disposable, they don't represent real items. Additionally, tracking items is
 * very inconsistent, so we must cache all components from an item, then periodically check to ensure these items
 * are still there, alongside all the item movement events available to us.
 *
 * ## Process:
 * - An Inventory component stores a cache of items, which we read and compare to actual items in the inventory.
 * - We go through geary items in the inventory and ensure the right items match our existing slots.
 * - If an item is a mismatch, we add it to a list of mismatches
 * - If an item isn't in our cache, we check the mismatches or deserialize it into the cache.
 * - All valid items get re-serialized TODO in the future there should be some form of dirty tag so we aren't unnecessarily serializing things
 */
object ItemTrackerSystem : TickingSystem(interval = 100) {
    @Suppress("UNREACHABLE_CODE")
    override fun tick() = Engine.runFor<Inventory> { inventoryComponent ->
        val (inventory, cache) = inventoryComponent
        val oldCache = cache.toMutableMap()
        val newCache = mutableMapOf<Int, Pair<ItemStack, Int>>()

        //TODO prevent issues with children and id changes
        inventory.forEachIndexed { i, item ->
            if (item == null || !item.hasItemMeta()) return@forEachIndexed
            val meta = item.itemMeta
            val container = meta.persistentDataContainer
            if (!container.isGearyEntity) return //TODO perhaps some way of knowing this without cloning the ItemMeta
            var itemEntityId = -1 //TODO try not to use a mutable var here

            //if the items match exactly, encode components
            if (item == oldCache[i]?.first) {
                itemEntityId = oldCache[i]!!.second
                oldCache.remove(i)
                container.encodeComponents(Engine.getComponentsFor(itemEntityId))
            } else {
                //if our old list of items still contains an item equal to this, simply update the indices
                oldCache.entries.find { it.value.first == item }?.let {
                    itemEntityId = it.value.second
                    oldCache.remove(it.key)
                } ?: run { //if we didn't find an equal item, this must be a new one
                    val itemEntity = Engine.entity {
                        addComponents(container.decodeComponents())
                    }
                    addChild(itemEntity)
                    itemEntityId = itemEntity.gearyId
                }
            }

            //save the new encoded components to the actual item meta, and place them into the new cache
            item.itemMeta = meta
            newCache[i] = item to itemEntityId
        }
        oldCache.values.forEach {
            Engine.removeEntity(it.second)
        }

        inventoryComponent.itemCache = newCache
    }
}