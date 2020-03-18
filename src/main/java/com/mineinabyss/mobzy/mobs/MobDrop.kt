package com.mineinabyss.mobzy.mobs

import com.mineinabyss.idofront.items.editItemMeta
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import kotlin.math.roundToInt
import kotlin.random.Random

@Serializable
data class SerializableItemStack(
        @SerialName("type") private val _type: Material,
        @SerialName("amount") private val _amount: Int = 1,
        @SerialName("custom-model-data") private val _customModelData: Int? = null,
        @SerialName("display-name") private val _displayName: String? = null,
        @SerialName("localized-name") private val _localizedName: String? = null,
        @SerialName("unbreakable") private val _unbreakable: Boolean = false,
        @SerialName("lore") private val _lore: List<String>? = null,
        @SerialName("damage") private val _damage: Int = 0
) {
    fun toItemStack() = ItemStack(_type).editItemMeta {
        setCustomModelData(_customModelData)
        setDisplayName(_displayName)
        setLocalizedName(_localizedName)
        isUnbreakable = _unbreakable
        lore = _lore
        if (this is Damageable) {
            damage = _damage
        }
    }
}


@Serializable
data class MobDrop(
        val item: SerializableItemStack,
        @SerialName("min-amount") val minAmount: Int = 1,
        @SerialName("max-amount") val maxAmount: Int = 1,
        @SerialName("drop-chance") val dropChance: Double = 1.0) {

    /**
     * @return The amount of items to be dropped, or null if the drop does not succeed
     * TODO I'd like to use exactly what Minecraft's existing system is, but I can't seem to find a way to reuse that.
     */
    fun chooseDrop(lootingLevel: Int = 0): ItemStack? {
        val lootingPercent = lootingLevel / 100.0
        val lootingMaxAmount: Int = if (dropChance >= 0.5) (maxAmount + lootingLevel * Random.nextDouble()).roundToInt() else maxAmount
        val lootingDropChance = if (dropChance >= 0.10) dropChance * (10.0 + lootingLevel) / 10.0 else dropChance + lootingPercent
        return if (Random.nextDouble() < lootingDropChance) {
            val drop = item.toItemStack()
            drop.amount = if (lootingMaxAmount <= minAmount) minAmount else Random.nextInt(minAmount, lootingMaxAmount)
            drop
        } else null
    }
}
