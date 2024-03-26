package com.mineinabyss.mobzy.features.drops

import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.util.randomOrMin
import kotlinx.serialization.Serializable
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.inventory.ItemStack
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
 * @param lootingAffectsAmount Should looting affect amount dropped? If null, does so when dropChance >= 10%
 * @param lootingAffectsDropChance Should looting affect drop chance? If null, does so when dropChance < 10%
 */
@Serializable
data class Drop(
    val exp: @Serializable(with = IntRangeSerializer::class) IntRange? = null,
    val item: SerializableItemStack? = null,
    val cooked: SerializableItemStack? = null,
    val cookExp: Float = 0f,
    val cookTime: Int = 200,
    val amount: @Serializable(with = IntRangeSerializer::class) IntRange = 1..1,
    val dropChance: Double = 1.0,
    val ignoredCauses: List<DamageCause> = listOf(
        DamageCause.SUFFOCATION,
        DamageCause.DROWNING,
        DamageCause.DRYOUT,
        DamageCause.CRAMMING,
        DamageCause.FALL
    ),
    val lootingAffectsAmount: Boolean? = null,
    val lootingAffectsDropChance: Boolean? = null,
) {
    /** @return The amount of items to be dropped, or null if the drop does not succeed */
    // TODO I'd like to use exactly what Minecraft's existing system is, but I can't seem to find a way to reuse that.
    fun chooseDrop(lootingLevel: Int, fire: Boolean): ItemStack? {
        if (item == null) return null

        // From the wiki: Increases the maximum number of items for most common drops by 1 per level.
        val isCommonDrop = dropChance >= 0.1
        val amountWithLooting =
            if (lootingAffectsAmount ?: isCommonDrop) amount.first..(amount.last + lootingLevel)
            else amount

        // Also from wiki: Looting increases the chance of rare drops by making a second attempt to drop if the original attempt failed.
        // The success chance of this second attempt is level / (level + 1).
        // Looting also increases the chance of rare drops and equipment drops by 1 percentage point per level.

        // We don't re-roll, just increase the percent for now
        val lootingPercent = lootingLevel / 100.0
        val lootingDropChance =
            if (lootingAffectsDropChance ?: !isCommonDrop)
                dropChance + lootingPercent
            else dropChance


        return if (Random.nextDouble() < lootingDropChance) {
            (if (fire && cooked != null) cooked.toItemStack() else item.toItemStack()).apply {
                amount = amountWithLooting.randomOrMin()
            }
        } else null
    }
}
