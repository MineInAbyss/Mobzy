package com.mineinabyss.mobzy.features.bucketable

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.geary.papermc.helpers.spawnFromPrefab
import com.mineinabyss.looty.tracking.toGearyOrNull
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerBucketEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

//@AutoScan
//class BucketableSystem : Listener {
//
//    @EventHandler
//    fun PlayerBucketEntityEvent.cancelBucketEntity() {
//        if (!entity.toGeary().has<Bucketable>()) isCancelled = true
//    }
//
//    @EventHandler(ignoreCancelled = true)
//    fun PlayerInteractEntityEvent.onPickupMob() {
//        val mob = rightClicked.toGeary().get<Bucketable>() ?: return
//        val requiredBucket = Material.valueOf(mob.bucketLiquidRequired.toString() + "_BUCKET")
//        val item = mob.bucketItem.toItemStack()
//
//        if (!Material.values().contains(requiredBucket)) return
//        if (player.inventory.getItem(hand).type != requiredBucket) return
//
//        player.inventory.setItemInMainHand(item)
//        item.toGearyOrNull(player)?.getOrSetPersisting { mob } ?: return
//        rightClicked.remove()
//        isCancelled = true // Cancel vanilla behaviour
//    }
//
//    @EventHandler(ignoreCancelled = true) // Fires after the onPickupMob thus it places it aswell
//    fun PlayerInteractEvent.onEmptyMobzyBucket() {
//        if (action != Action.RIGHT_CLICK_BLOCK || hand != EquipmentSlot.HAND) return
//        val bucket = player.inventory.itemInMainHand.toGearyOrNull(player)?.get<Bucketable>() ?: return
//        val block = clickedBlock?.getRelative(blockFace) ?: return
//
//        block.type = bucket.bucketLiquidRequired
//        block.location.toCenterLocation().spawnFromPrefab(bucket.bucketMob)
//        player.inventory.setItemInMainHand(ItemStack(Material.BUCKET))
//        isCancelled = true // Cancel vanilla behaviour
//    }
//}
