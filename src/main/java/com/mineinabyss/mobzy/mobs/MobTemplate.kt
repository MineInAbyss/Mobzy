package com.mineinabyss.mobzy.mobs

import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.mobzy.api.nms.typeinjection.NMSAttributeBuilder
import com.mineinabyss.mobzy.api.nms.typeinjection.NMSAttributes
import com.mineinabyss.mobzy.api.nms.typeinjection.set
import com.mineinabyss.mobzy.registration.MobzyTemplates
import com.mineinabyss.mobzy.registration.MobzyTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R1.GenericAttributes.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * A class which stores information on mobs that can be deserialized from the config.
 */
@Serializable
data class MobTemplate(
        @SerialName("name") private val _name: String? = null,
        val model: Int,
        val attributes: MobAttributes = MobAttributes(),
        @SerialName("model-material") val modelMaterial: Material = Material.DIAMOND_SWORD,
        @SerialName("tempt-items") val temptItems: List<Material>? = null,
        @SerialName("min-exp") val minExp: Int? = null,
        @SerialName("max-exp") val maxExp: Int? = null,
        @SerialName("adult") val isAdult: Boolean = true,
        @SerialName("death-commands") val deathCommands: List<String> = ArrayList(),
        val drops: List<MobDrop> = listOf()) {
    val name by lazy { _name ?: MobzyTemplates.getNameForTemplate(this) }
    val type get() = MobzyTypes[name]

    fun chooseDrops(looting: Int = 0, fire: Int = 0): List<ItemStack?> = drops.toList().map { it.chooseDrop(looting, fire) }

    val modelItemStack
        get() = ItemStack(modelMaterial).editItemMeta {
            setCustomModelData(model)
        }
}

@Serializable
data class MobAttributes(
        @SerialName("max-health") val maxHealth: Double? = null,
        @SerialName("movement-speed") val movementSpeed: Double? = null,
        @SerialName("follow-range") val followRange: Double? = null,
        @SerialName("attack-damage") val attackDamage: Double? = null
) {
    fun toNMSBuilder(): NMSAttributeBuilder = NMSAttributes.forEntityInsentient()
            .set(MAX_HEALTH, maxHealth)
            .set(MOVEMENT_SPEED, movementSpeed)
            .set(FOLLOW_RANGE, followRange)
            .set(ATTACK_DAMAGE, attackDamage)
}