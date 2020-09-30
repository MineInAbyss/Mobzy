package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.components.*
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.entity
import com.mineinabyss.geary.ecs.engine.forEach
import com.mineinabyss.geary.ecs.remove
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.looty.ecs.components.Held
import com.mineinabyss.looty.ecs.components.Inventory
import com.mineinabyss.looty.ecs.components.PlayerComponent
import com.mineinabyss.mobzy.ecs.geary
import com.mineinabyss.mobzy.ecs.get
import com.mineinabyss.mobzy.ecs.store.decodeComponents
import com.mineinabyss.mobzy.ecs.store.encodeComponents
import com.mineinabyss.mobzy.ecs.store.isGearyEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
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
    override fun tick() = Engine.forEach<PlayerComponent, Inventory> { (player), inventoryComponent ->
        //TODO make children use an engine too, then easily remove all held components
        updateAndSaveItems(player, inventoryComponent)

        //Add a held component to currently held item
        inventoryComponent.entityAt(player.inventory.heldItemSlot)?.addComponent(Held())
    }

    fun Inventory.unregisterAll() {
        itemCache.values.forEach { (_, entity) ->
            entity.remove()
        }
        itemCache.clear()
    }

    fun GearyEntity.updateAndSaveItems(player: Player, inventoryComponent: Inventory) {
        val oldCache = inventoryComponent.itemCache.toMutableMap()
        val newCache = mutableMapOf<Int, Pair<ItemStack, GearyEntity>>()
        val heldSlot = player.inventory.heldItemSlot
        //TODO prevent issues with children and id changes
        player.inventory.forEachIndexed { i, item ->
            if (item == null || !item.hasItemMeta()) return@forEachIndexed
            val meta = item.itemMeta
            val container = meta.persistentDataContainer
            if (!container.isGearyEntity) return //TODO perhaps some way of knowing this without cloning the ItemMeta

            //if the items match exactly, encode components
            val itemEntity: GearyEntity = if (item == oldCache[i]?.first) {
                val entity = oldCache[i]!!.second
                oldCache.remove(i)
                container.encodeComponents(Engine.getComponentsFor(entity.gearyId))
                entity
            } else {
                //if our old list of items still contains an item equal to this, simply update the indices
                oldCache.entries.find { it.value.first == item }?.let {
                    val entity = it.value.second
                    oldCache.remove(it.key)
                    entity
                } ?: run { //if we didn't find an equal item, this must be a new one
                    val entity = Engine.entity new@{
                        addComponents(container.decodeComponents())
                    }
                    addChild(entity)
                    entity
                }
            }
            //TODO custom item config system
            if (i != heldSlot) itemEntity.removeComponent<Held>()

            //save the new encoded components to the actual item meta, and place them into the new cache
            //TODO dont save if no changes found
            item.itemMeta = meta
            newCache[i] = item to itemEntity
        }
        oldCache.values.forEach {
            it.second.remove()
        }

        inventoryComponent.itemCache = newCache
    }

    @EventHandler
    fun itemMoveEvent(e: InventoryClickEvent) {
        val cursor = e.cursor //ADD: the item that was put into the slot
        val currItem = e.currentItem //REMOVE: the item that was clicked and its slot

        val player = e.whoClicked
        val inventory = player.get<Inventory>() ?: return

        player.info("""
            Cursor: ${e.cursor}
            CurrentItem: ${e.currentItem}
            Slot: ${e.slot}
        """.trimIndent())

        //shift clicking still considers cursor null and we don't know the location being put into TODO try to see if we can find it
        if (e.isShiftClick) return

        //TODO if both cursor and currItem aren't null, we try to swap them instead of removing
        if (cursor == null) { //remove if cursor had nothing
            inventory.entityAt(e.slot)?.remove()
            inventory.itemCache.remove(e.slot)

        } else { //add otherwise
            val meta = cursor.itemMeta
            if (!meta.persistentDataContainer.isGearyEntity) return

            val entity = Engine.entity {
                addComponents(meta.persistentDataContainer.decodeComponents())
            }
            inventory.itemCache[e.slot] = cursor to entity
        }
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
        geary(player)?.get<Inventory>()?.swapHeldComponent(prevSlot, newSlot)
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
        val inventory = geary(player)?.get<Inventory>() ?: return

        val mainHandSlot = player.inventory.heldItemSlot
        val offHandSlot = 40 //TODO is there a version safe way of getting this slot via enum or something?

        inventory.swapSlotCache(mainHandSlot, offHandSlot)

        //we always want to remove from offhand and add into main hand
        inventory.swapHeldComponent(removeFrom = offHandSlot, addTo = mainHandSlot)
    }

    //TODO dropping items should serialize them and instantly remove from inv
    /**
     * There's no way of knowing which slot an item was in when dropped, so the most reliable way of ensuring nothing
     * funky happens is recalculating everything. Try and improve on this later!
     */
    @EventHandler
    fun onDropItem(e: PlayerDropItemEvent) {
        val (player) = e
        geary(player) {
            with<Inventory> {
                updateAndSaveItems(player, it)
            }
        }
    }
}