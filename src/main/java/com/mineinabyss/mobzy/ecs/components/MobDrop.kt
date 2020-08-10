package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.recpies.addCookingRecipes
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.mobzy.mobzy
import kotlinx.serialization.Serializable
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack
import kotlin.math.roundToInt
import kotlin.random.Random

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

    /**
     * @return The amount of items to be dropped, or null if the drop does not succeed
     * TODO I'd like to use exactly what Minecraft's existing system is, but I can't seem to find a way to reuse that.
     */
    fun chooseDrop(lootingLevel: Int = 0, fire: Int = 0): ItemStack? {
        val lootingPercent = lootingLevel / 100.0
        val lootingMaxAmount: Int = if (dropChance >= 0.5) (maxAmount + lootingLevel * Random.nextDouble()).roundToInt() else maxAmount
        val lootingDropChance = if (dropChance >= 0.10) dropChance * (10.0 + lootingLevel) / 10.0 else dropChance + lootingPercent
        return if (Random.nextDouble() < lootingDropChance) {
            val drop = if (fire > 0 && cooked != null) cooked.toItemStack() else item.toItemStack()
            drop.amount = if (lootingMaxAmount <= minAmount) minAmount else Random.nextInt(minAmount, lootingMaxAmount)
            drop
        } else null
    }
}
