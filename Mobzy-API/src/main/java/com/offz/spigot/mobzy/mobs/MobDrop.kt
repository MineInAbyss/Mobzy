package com.offz.spigot.mobzy.mobs

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack

//TODO calculate looting
data class MobDrop(val item: ItemStack,
                   val minAmount: Int = 1,
                   val maxAmount: Int = 1,
                   val dropChance: Double = 1.0) : ConfigurationSerializable {
    @JvmOverloads
    constructor(material: Material, minAmount: Int = 1, maxAmount: Int = 1, dropChance: Double = 0.0) : this(ItemStack(material), minAmount, maxAmount, dropChance)

    fun chooseDrop(): ItemStack? {
        if (Math.random() < dropChance) {
            val drop = item.clone()
            drop.amount = (Math.random() * (maxAmount - minAmount) + minAmount).toInt()
            return drop
        }
        return null
    }

    override fun serialize(): Map<String, Any> {
        return mapOf()
    }

    companion object {

        /**
         * Required method for configuration serialization
         *
         * @param args map to deserialize
         * @return deserialized item stack
         * @see ConfigurationSerializable
         */
        fun deserialize(args: Map<String, Any>): MobDrop? {
            val item = (if (args.containsKey("item"))
                args["item"] as ItemStack
            else if (args.containsKey("material"))
                try {
                    ItemStack(Material.getMaterial(args["material"] as String)!!)
                } catch (e: Exception) {
                    Bukkit.getConsoleSender().sendMessage("Deserializing $args")
                    e.printStackTrace()
                    return null
                }
            else null) ?: return null

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
