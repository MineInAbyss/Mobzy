package com.mineinabyss.mobzy.mobs

import kotlinx.serialization.*
import kotlinx.serialization.internal.PrimitiveDescriptor
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.internal.StringDescriptor
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

@Serializer(forClass = ItemStack::class)
object SpigotSerializer : KSerializer<ItemStack> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("ItemStack")

    override fun serialize(encoder: Encoder, obj: ItemStack) {
        obj.serialize()
        val yamlConfig = YamlConfiguration()
        yamlConfig.set("", obj) //TODO don't know if this will set it like I think it will
        encoder.encodeString(yamlConfig.saveToString())
    }

    override fun deserialize(decoder: Decoder): ItemStack {
        val yamlConfig = YamlConfiguration()
        yamlConfig.loadFromString(decoder.decodeString())
        return yamlConfig.get("") as ItemStack
    }
}

@Serializer(forClass = Date::class)
object DateSerializer: KSerializer<Date> {
    private val df: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS")

    override val descriptor: SerialDescriptor = StringDescriptor

    override fun serialize(encoder: Encoder, obj: Date) {
        encoder.encodeString(df.format(obj))
    }

    override fun deserialize(decoder: Decoder): Date {
        return df.parse(decoder.decodeString())
    }
}

@Serializable
data class MobDrop(
        @Serializable(with = SpigotSerializer::class) val item: ItemStack? = null,
        val material: Material? = null,
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
            val drop = ItemStack(Material.STONE)//TODO item.clone()
            drop.amount = if (lootingMaxAmount <= minAmount) minAmount else Random.nextInt(minAmount, lootingMaxAmount)
            drop
        } else null
    }
}
