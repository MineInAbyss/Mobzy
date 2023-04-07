package com.mineinabyss.mobzy.features.deathloot

import com.mineinabyss.geary.helpers.with
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.mobzy.features.deathloot.components.DeathLoot
import net.minecraft.world.entity.Saddleable
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

class DeathLootSystem: Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun EntityDeathEvent.setCustomDeathLoot() {
        val gearyEntity = entity.toGearyOrNull() ?: return

        gearyEntity.with { deathLoot: DeathLoot ->
            drops.clear()
            droppedExp = 0

            // Drop equipped items from rideable entity
            if(entity is Saddleable) drops.add(ItemStack(Material.SADDLE))
            if (entity.lastDamageCause?.cause !in deathLoot.ignoredCauses) {
                deathLoot.expToDrop()?.let { droppedExp = it }
                val heldItem = entity.killer?.inventory?.itemInMainHand
                val looting = heldItem?.enchantments?.get(Enchantment.LOOT_BONUS_MOBS) ?: 0
                drops.addAll(deathLoot.drops.mapNotNull { it.chooseDrop(looting, entity.fireTicks > 0) })
            }
        }
    }
}
