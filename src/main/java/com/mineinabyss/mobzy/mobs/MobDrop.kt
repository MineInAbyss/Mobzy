package com.mineinabyss.mobzy.mobs

import com.mineinabyss.idofront.items.editItemMeta
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import kotlin.math.roundToInt
import kotlin.random.Random

data class MobDrop(val item: ItemStack,
                   val minAmount: Int = 1,
                   val maxAmount: Int = 1,
                   val dropChance: Double = 1.0) : ConfigurationSerializable {
    /**
     * @return The amount of items to be dropped, or null if the drop does not succeed
     * TODO I'd like to use exactly what Minecraft's existing system is, but I can't seem to find a way to reuse that.
     */
    fun chooseDrop(lootingLevel: Int = 0): ItemStack? {
        val lootingPercent = lootingLevel / 100.0
        val lootingMaxAmount: Int = if (dropChance >= 0.5) (maxAmount + lootingLevel * Random.nextDouble()).roundToInt() else maxAmount
        val lootingDropChance = if (dropChance >= 0.10) dropChance * (10.0 + lootingLevel) / 10.0 else dropChance + lootingPercent
        return if (Random.nextDouble() < lootingDropChance) {
            val drop = item.clone()
            drop.amount = if (lootingMaxAmount <= minAmount) minAmount else Random.nextInt(minAmount, lootingMaxAmount)
            drop
        } else null
    }

    override fun serialize(): Map<String, Any> = TODO("No serialization implementation yet")

    companion object {

        //TODO convert to use kotlinx.serialization
        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        fun deserialize(args: Map<String, Any>): MobDrop? {
            val item = when {
                args.containsKey("item") -> args["item"] as ItemStack
                args.containsKey("material") -> try {
                    ItemStack(Material.getMaterial(args["material"] as String)!!)
                } catch (e: Exception) {
                    Bukkit.getConsoleSender().sendMessage("Deserializing $args")
                    e.printStackTrace()
                    return null
                }
                else -> null
            } ?: return null

            if (args.containsKey("custom-model-data")) item.editItemMeta {
                setCustomModelData((args["custom-model-data"] as Int))
            }

            val dropChance = if (args.containsKey("drop-chance"))
                args["drop-chance"] as Double
            else 1.0

            if (args.containsKey("amount")) {
                val amount = args["amount"] as Int
                return MobDrop(item, amount, amount, dropChance)
            }

            if (!(args.containsKey("min-amount") && args.containsKey("max-amount")))
                return MobDrop(item, 1, 1, dropChance)

            val minAmount = args["min-amount"] as Int
            val maxAmount = args["max-amount"] as Int

            return MobDrop(item, minAmount, maxAmount, dropChance)
        }
    }
}
