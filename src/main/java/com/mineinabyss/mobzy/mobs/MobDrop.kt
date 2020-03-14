package com.mineinabyss.mobzy.mobs

import com.mineinabyss.idofront.items.editItemMeta
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.reflect.KClass

@Serializer(forClass = ConfigurationSerializable::class)
class SpigotSerializer<T : ConfigurationSerializable>(private val serializableClass: KClass<T>) : KSerializer<T> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl(serializableClass.simpleName!!)

    override fun serialize(encoder: Encoder, obj: T) {
        val yamlConfig = YamlConfiguration()
        yamlConfig.set("", obj) //TODO don't know if this will set it like I think it will
        encoder.encodeString(yamlConfig.saveToString())
    }

    override fun deserialize(decoder: Decoder): T {
        val yamlConfig = YamlConfiguration()
        yamlConfig.loadFromString(decoder.decodeString())
        return yamlConfig.get("") as T
    }
}

@Serializable
data class MobDrop(//@Serializable(with = SpigotSerializer::class) val item: ItemStack,
//                   val material: Material,
        val minAmount: Int = 1,
        val maxAmount: Int = 1,
        val dropChance: Double = 1.0) {
    /**
     * @return The amount of items to be dropped, or null if the drop does not succeed
     * TODO I'd like to use exactly what Minecraft's existing system is, but I can't seem to find a way to reuse that.
     */
    fun chooseDrop(lootingLevel: Int = 0): ItemStack? {
        val lootingPercent = lootingLevel / 100.0
        val lootingMaxAmount: Int = if (dropChance >= 0.5) (maxAmount + lootingLevel * Random.nextDouble()).roundToInt() else maxAmount
        val lootingDropChance = if (dropChance >= 0.10) dropChance * (10.0 + lootingLevel) / 10.0 else dropChance + lootingPercent
        return if (Random.nextDouble() < lootingDropChance) {
            val drop = ItemStack(Material.STONE)//TODO item.clone()
            drop.amount = if (lootingMaxAmount <= minAmount) minAmount else Random.nextInt(minAmount, lootingMaxAmount)
            drop
        } else null
    }
}
