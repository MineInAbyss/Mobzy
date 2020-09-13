package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.components.*
import com.mineinabyss.geary.ecs.geary
import com.mineinabyss.geary.ecs.remove
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.looty.ecs.components.Held
import com.mineinabyss.looty.ecs.components.Inventory
import com.mineinabyss.looty.ecs.components.PlayerComponent
import com.mineinabyss.mobzy.ecs.ecs
import com.mineinabyss.mobzy.ecs.store.decodeComponents
import com.mineinabyss.mobzy.ecs.store.encodeComponents
import com.mineinabyss.mobzy.ecs.store.isGearyEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import kotlin.collections.set

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
object ItemTrackerSystem : TickingSystem(interval = 100), Listener {
    override fun tick() = Engine.runFor<PlayerComponent, Inventory> { (player), inventoryComponent ->
        //TODO remove all held components from all items just in case

        updateAndSaveItems(player, inventoryComponent)

        //Add a held component to currently held item
        inventoryComponent.entityAt(player.inventory.heldItemSlot)?.addComponent(Held())
    }

    fun Inventory.unregisterAll() {
        itemCache.values.forEach { (_, id) ->
            geary(id) {
                remove()
            }
        }
        itemCache.clear()
    }

    fun GearyEntity.updateAndSaveItems(player: Player, inventoryComponent: Inventory) {
        val oldCache = inventoryComponent.itemCache.toMutableMap()
        val newCache = mutableMapOf<Int, Pair<ItemStack, Int>>()

        //TODO prevent issues with children and id changes
        player.inventory.forEachIndexed { i, item ->
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
            //TODO dont save if no changes found
            item.itemMeta = meta
            newCache[i] = item to itemEntityId
        }
        oldCache.values.forEach {
            Engine.removeEntity(it.second)
        }

        inventoryComponent.itemCache = newCache
    }

    @EventHandler
    fun itemMoveEvent(e: InventoryClickEvent) {
        e.whoClicked.info("""
            Cursor: ${e.cursor}
            CurrentItem: ${e.currentItem}
            Slot: ${e.slot}
        """.trimIndent())
    }

    /** Immediately adds a held component to the currently held item. */
    //TODO another component for when in offhand
    //TODO remove held when swapping into offhand
    @EventHandler
    fun onHeldItemSwap(e: PlayerItemHeldEvent) {
//        val (player, prevSlot, newSlot) = e //TODO switch to this when updating idofront
        val player = e.player
        val prevSlot = e.previousSlot
        val newSlot = e.newSlot
        player.ecs?.get<Inventory>()?.swapHeldComponent(prevSlot, newSlot)
    }

    fun Inventory.swapHeldComponent(removeFrom: Int, addTo: Int) {
        entityAt(removeFrom)?.removeComponent<Held>()
        entityAt(addTo)?.addComponent(Held())
    }

    fun Inventory.swapSlotCache(first: Int, second: Int) {
        val firstCache = itemCache[first]
        val secondCache = itemCache[second]

         if (firstCache == null)
            itemCache.remove(second)
        else
            itemCache[second] = firstCache
        if (secondCache == null)
            itemCache.remove(first)
        else
            itemCache[first] = secondCache
    }

    @EventHandler
    fun onSwapItems(e: PlayerSwapHandItemsEvent) {
        val (player) = e
        val inventory = player.ecs?.get<Inventory>() ?: return

        val mainHandSlot = player.inventory.heldItemSlot
        val offHandSlot = 40 //TODO is there a version safe way of getting this slot via enum or something?

        inventory.swapSlotCache(mainHandSlot, offHandSlot)

        //we always want to remove from offhand and add into main hand
        inventory.swapHeldComponent(removeFrom = offHandSlot, addTo = mainHandSlot)
    }

    //TODO dropping items should serialize them and instantly remove from inv
}