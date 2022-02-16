package com.mineinabyss.mobzy.systems.listeners

import androidx.compose.runtime.Composable
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.guiy.components.Item
import com.mineinabyss.guiy.components.canvases.Chest
import com.mineinabyss.guiy.inventory.GuiyOwner
import com.mineinabyss.guiy.modifiers.Modifier
import com.mineinabyss.guiy.modifiers.at
import com.mineinabyss.guiy.modifiers.clickable
import com.mineinabyss.guiy.modifiers.height
import com.mineinabyss.idofront.font.Space
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.serialization.toSerializable
import com.mineinabyss.mobzy.ecs.components.interaction.Rideable
import com.mineinabyss.mobzy.ecs.components.interaction.Tamable
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

sealed class MountInventoryScreen(val title: String, val height: Int) {
    object Default : MountInventoryScreen("${Space.of(-18)}${ChatColor.WHITE}:orthbanking_menu:", 4)
    object Deposit : MountInventoryScreen("${Space.of(-18)}${ChatColor.WHITE}:orthbanker_deposit_menu:", 5)
    object Widthdraw : MountInventoryScreen("${Space.of(18)}${ChatColor.WHITE}:orthbanker_withdrawal_menu:", 5)
}

@Composable
fun GuiyOwner.MountInventoryMenu(player: Player, mount: Entity) {
    Chest(setOf(player), "Mount Inventory", Modifier.height(3),
        onClose = {
            player.closeInventory()
        })
    {
        val rideable = mount.toGeary().get<Rideable>()
        val tamable = mount.toGeary().get<Tamable>()

        Item(ItemStack(Material.LEATHER_HORSE_ARMOR), Modifier.at(0, 0).clickable {
            player.inventory.contents.forEach { item ->
                if (rideable?.allowedArmor?.contains(item.toSerializable()) == true) {
                    item.subtract(1)
                    player.playSound(player.location, Sound.ENTITY_HORSE_ARMOR, 1f, 1f)
                    mount.toGeary().setPersisting(rideable.hasArmor)
                    mount.toGeary().setPersisting(rideable.armor == item.toSerializable())
//                    mount.toGeary().getOrSetPersisting {
//                        rideable.hasArmor
//                        rideable.armor = item.toSerializable()
//                        broadcast(rideable.armor)
//                    }

                    return@clickable
                } else player.error("You need ${item.displayName()} for this!")
            }
        })

        Item(ItemStack(Material.SADDLE), Modifier.at(0, 1).clickable {
            player.inventory.contents.forEach { item ->
                if (item.type == Material.SADDLE) {
                    item.subtract(1)
                    //rideable?.isSaddled = true
                    player.playSound(player.location, Sound.ENTITY_HORSE_SADDLE, 1f, 1f)
                    mount.toGeary().setPersisting(rideable!!.isSaddled)
                    /*mount.toGeary().getOrSetPersisting {
                        rideable?.isSaddled ?: return@clickable
                    }*/

                    return@clickable
                } else player.error("You need a saddle for this!")
            }

        })

        Item(ItemStack(Material.CHEST), Modifier.at(0, 2).clickable {
            player.inventory.contents.forEach { item ->
                if (item.type == Material.CHEST) {
                    item.subtract(1)
                    //rideable?.hasStorage = true
                    player.playSound(player.location, Sound.ENTITY_DONKEY_CHEST, 1f, 1f)
                    mount.toGeary().setPersisting(rideable!!.hasStorage)
                    /*mount.toGeary().getOrSetPersisting {
                        rideable?.hasStorage ?: return@clickable
                    }*/

                    return@clickable
                } else player.error("You need a chest for this!")
            }
        })
    }
}