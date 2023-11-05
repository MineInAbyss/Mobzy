package com.mineinabyss.mobzy.features.bucketable

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBucketEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

@AutoScan
class BucketableSystem : Listener {
    @EventHandler
    fun PlayerBucketEntityEvent.cancelBucketEntity() {
        if (!entity.toGeary().has<Bucketable>()) isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun PlayerInteractEntityEvent.onPickupMob() {
        val bucketable = rightClicked.toGeary().get<Bucketable>() ?: return
        val requiredBucket = Material.valueOf(bucketable.bucketLiquidRequired.toString() + "_BUCKET")
        val item = bucketable.bucketItem.toItemStack()

        if (!Material.values().contains(requiredBucket)) return
        if (player.inventory.getItem(hand).type != requiredBucket) return

        player.inventory.setItemInMainHand(item)
        rightClicked.remove()
        isCancelled = true // Cancel vanilla behaviour
    }
}
