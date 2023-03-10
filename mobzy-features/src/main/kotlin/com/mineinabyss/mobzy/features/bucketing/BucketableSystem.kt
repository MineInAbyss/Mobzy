package com.mineinabyss.mobzy.features.bucketing

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.geary.papermc.tracking.items.gearyItemInMainHand
import com.mineinabyss.mobzy.helpers.spawnFromPrefab
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerBucketEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

@AutoScan
class BucketableSystem : Listener {

    @EventHandler
    fun PlayerBucketEntityEvent.cancelBucketEntity() {
        if (!entity.toGeary().has<Bucketable>()) isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun PlayerInteractEntityEvent.onPickupMob() {
        val clickedEntity = rightClicked.toGeary()
        val bucketable = clickedEntity.get<Bucketable>() ?: return
        val requiredBucket = Material.valueOf(bucketable.bucketLiquidRequired.toString() + "_BUCKET")

        if (!Material.values().contains(requiredBucket)) return
        if (player.inventory.getItem(hand).type != requiredBucket) return

        val bucketItem = bucketable.bucketItem.toItemStack()
        player.inventory.setItemInMainHand(bucketItem)
        player.gearyItemInMainHand?.getOrSetPersisting { bucketable } ?: return
        rightClicked.remove()
        isCancelled = true // Cancel vanilla behaviour
    }

    @EventHandler(ignoreCancelled = true) // Fires after the onPickupMob thus it places it aswell
    fun PlayerInteractEvent.onEmptyMobzyBucket() {
        if (action != Action.RIGHT_CLICK_BLOCK || hand != EquipmentSlot.HAND) return

        val bucket = player.gearyItemInMainHand?.get<Bucketable>() ?: return
        val block = clickedBlock?.getRelative(blockFace) ?: return

        block.type = bucket.bucketLiquidRequired
        block.location.toCenterLocation().spawnFromPrefab(bucket.bucketMob)
        player.inventory.setItemInMainHand(ItemStack(Material.BUCKET))
        isCancelled = true // Cancel vanilla behaviour
    }
}
