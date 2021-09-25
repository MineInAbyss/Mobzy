package com.mineinabyss.mobzy.ecs.components.death

import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.recipes.addCookingRecipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.mobzy.mobzy
import kotlinx.serialization.Serializable
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * A serializable class for defining an item a mob can drop. It acts as a sort of factory for [ItemStack]s that may
 * define rarity, how many items to spawn, etc...
 *
 * Also allows defining a cooked version of the item that will automatically be registered when the class is created.
 *
 * @param item The item to be spawned.
 * @param cooked The cooked version of this item.
 * @param cookExp The amount of exp to drop when this item is cooked.
 * @param cookTime How long it takes for this item to cook
 * @param minAmount The minimum stack size of this item.
 * @param maxAmount The maximum stack size of this item.
 * @param dropChance A chance from 0 to 1 for this item to be dropped.
 *
 */
@Serializable
data class MobDrop(
    val item: SerializableItemStack,
    val cooked: SerializableItemStack? = null,
    val cookExp: Float = 0f,
    val cookTime: Int = 200,
    val minAmount: Int = 1,
    val maxAmount: Int = 1,
    val dropChance: Double = 1.0
) {
    init {
        if (cooked != null) {
            val cookedItem = cooked.toItemStack()
            //TODO pretty hacky way of adding recipes. Won't reload them from config if they've changed.
            try {
                addCookingRecipes(
                    ChatColor.stripColor(cookedItem.itemMeta!!.displayName.color('§').replace(' ', '_'))!!,
                    item.toItemStack(),
                    cookedItem,
                    cookExp,
                    cookTime,
                    mobzy
                )
            } catch (e: IllegalStateException) {
            }
        }
    }

    /** @return The amount of items to be dropped, or null if the drop does not succeed */
    // TODO I'd like to use exactly what Minecraft's existing system is, but I can't seem to find a way to reuse that.
    fun chooseDrop(lootingLevel: Int, fire: Boolean): ItemStack? {
        val lootingPercent = lootingLevel / 100.0

        val lootingMaxAmount: Int =
            if (dropChance >= 0.5)
                (maxAmount + lootingLevel * Random.nextDouble()).roundToInt()
            else maxAmount

        val lootingDropChance =
            if (dropChance >= 0.10)
                dropChance * (10.0 + lootingLevel) / 10.0
            else dropChance + lootingPercent

        return if (Random.nextDouble() < lootingDropChance) {
            val drop =
                if (fire && cooked != null)
                    cooked.toItemStack()
                else item.toItemStack()
            drop.amount =
                if (lootingMaxAmount <= minAmount) minAmount
                else Random.nextInt(minAmount, lootingMaxAmount)
            drop
        } else null
    }
}
