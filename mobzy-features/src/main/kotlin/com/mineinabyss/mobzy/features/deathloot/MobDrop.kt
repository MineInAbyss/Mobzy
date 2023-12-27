package com.mineinabyss.mobzy.features.deathloot

import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.Serializable
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
 * @param amount A range for the stack size of this item.
 * @param dropChance A chance from 0 to 1 for this item to be dropped.
 *
 */
@Serializable
data class MobDrop(
    val item: SerializableItemStack,
    val cooked: SerializableItemStack? = null,
    val cookExp: Float = 0f,
    val cookTime: Int = 200,
    val amount: @Serializable(with = IntRangeSerializer::class) IntRange = 1..1,
    val dropChance: Double = 1.0
) {
    /** @return The amount of items to be dropped, or null if the drop does not succeed */
    // TODO I'd like to use exactly what Minecraft's existing system is, but I can't seem to find a way to reuse that.
    fun chooseDrop(lootingLevel: Int, fire: Boolean): ItemStack? {
        val lootingPercent = lootingLevel / 100.0

        val lootingMaxAmount: Int =
            if (dropChance >= 0.5)
                (amount.last + lootingLevel * Random.nextDouble()).roundToInt()
            else amount.last

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
                if (lootingMaxAmount <= amount.first) amount.first
                else Random.nextInt(amount.first, lootingMaxAmount)
            drop
        } else null
    }
}
