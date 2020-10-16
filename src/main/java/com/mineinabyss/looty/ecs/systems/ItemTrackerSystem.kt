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
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.Held
import com.mineinabyss.looty.ecs.components.LootyEntity
import com.mineinabyss.looty.ecs.components.PlayerComponent
import com.mineinabyss.mobzy.ecs.geary
import com.mineinabyss.mobzy.ecs.get
import com.mineinabyss.mobzy.ecs.store.decodeComponents
import com.mineinabyss.mobzy.ecs.store.encodeComponents
import com.mineinabyss.mobzy.ecs.store.isGearyEntity
import com.mineinabyss.mobzy.ecs.with
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.PlayerInventory
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
    override fun tick() = Engine.forEach<PlayerComponent, ChildItemCache> { (player), inventoryComponent ->
        //TODO make children use an engine too, then easily remove all held components
        inventoryComponent.updateAndSaveItems(player.inventory, this)

        //Add a held component to currently held item
        inventoryComponent[player.inventory.heldItemSlot]?.addComponent(Held())
    }

    fun ChildItemCache.updateAndSaveItems(inventory: PlayerInventory, gearyEntity: GearyEntity) {
        val oldCache = toMutableMap()
        val newCache = mutableMapOf<Int, LootyEntity>()
        val heldSlot = inventory.heldItemSlot
        //TODO prevent issues with children and id changes

        inventory.forEachIndexed { i, item ->
            if (item == null || !item.hasItemMeta()) return@forEachIndexed
            val meta = item.itemMeta
            val container = meta.persistentDataContainer
            if (!container.isGearyEntity) return@forEachIndexed //TODO perhaps some way of knowing this without cloning the ItemMeta

            //if the items match exactly, encode components
            val cachedItemEntity = oldCache[i]
            val itemEntity: GearyEntity = if (item == cachedItemEntity?.item) {
                oldCache.remove(i)
                container.encodeComponents(cachedItemEntity.getComponents())
                cachedItemEntity
            } else {
                //if our old list of items still contains an item equal to this, simply update the indices
                val equivalent = oldCache.entries.find { it.value.item == item }
                if (equivalent != null) {
                    oldCache.remove(equivalent.key)
                    equivalent.value
                } else { //if we didn't find an equal item, this must be a new one
                    val entity = Engine.entity new@{ //TODO do we need new@?
                        addComponents(container.decodeComponents())
                    }
                    gearyEntity.addChild(entity)
                    entity
                }
            }
            //TODO custom item config system
            if (i != heldSlot) itemEntity.removeComponent<Held>()

            //save the new encoded components to the actual item meta, and place them into the new cache
            //TODO dont save if no changes found
            item.itemMeta = meta
            newCache[i] = LootyEntity(item, itemEntity)
        }
        oldCache.values.forEach {
            it.remove()
        }

        update(newCache)
    }

    @EventHandler
    fun itemMoveEvent(e: InventoryClickEvent) {
        val cursor = e.cursor //ADD: the item that was put into the slot
        val currItem = e.currentItem //REMOVE: the item that was clicked and its slot

        val player = e.whoClicked
        val inventory = player.get<ChildItemCache>() ?: return

        player.info("""
            Cursor: ${e.cursor}
            CurrentItem: ${e.currentItem}
            Slot: ${e.slot}
        """.trimIndent())

        //shift clicking still considers cursor null and we don't know the location being put into TODO try to see if we can find it
        if (e.isShiftClick) return

        //TODO if both cursor and currItem aren't null, we try to swap them instead of removing
        if (cursor == null || cursor.type == Material.AIR) { //remove if cursor had nothing
            inventory.remove(e.slot)
        } else if(cursor.hasItemMeta()) { //otherwise, cursor into cache
            val meta = cursor.itemMeta
            if (!meta.persistentDataContainer.isGearyEntity) return

            val entity = Engine.entity {
                addComponents(meta.persistentDataContainer.decodeComponents())
            }
            //TODO adding child should be done within inv
            geary(player)?.addChild(entity)
            inventory[e.slot] = LootyEntity(cursor, entity)
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
        player.get<ChildItemCache>()?.swapHeldComponent(prevSlot, newSlot)
    }

    private const val offHandSlot = 40 //TODO is there a version safe way of getting this slot via enum or something?

    @EventHandler
    fun onSwapOffhand(e: PlayerSwapHandItemsEvent) {
        val (player) = e
        player.with<ChildItemCache> { inventory ->
            val mainHandSlot = player.inventory.heldItemSlot

            inventory.swapSlotCache(mainHandSlot, offHandSlot)

            //we always want to remove from offhand and add into main hand
            inventory.swapHeldComponent(removeFrom = offHandSlot, addTo = mainHandSlot)
        }

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
            with<ChildItemCache> {
                it.updateAndSaveItems(player.inventory, this)
            }
        }
    }
}