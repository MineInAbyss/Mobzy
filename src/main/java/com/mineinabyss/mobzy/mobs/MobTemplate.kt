package com.mineinabyss.mobzy.mobs

import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.mobzy.mobzy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * TODO make this into a proper immutable builder.
 * A class which stores information on mobs that can be deserialized from the config.
 */
@Serializable
data class MobTemplate(
        @SerialName("name") private val _name: String? = null,
        val model: Int,
        @SerialName("model-material") val modelMaterial: Material = Material.DIAMOND_SWORD,
        @SerialName("tempt-items") val temptItems: List<Material>? = null,
        @SerialName("max-health") val maxHealth: Double? = null,
        @SerialName("movement-speed") val movementSpeed: Double? = null,
        @SerialName("follow-range") val followRange: Double? = null,
        @SerialName("attack-damage") val attackDamage: Double? = null,
        @SerialName("min-exp") val minExp: Int? = null,
        @SerialName("max-exp") var maxExp: Int? = null, //TODO if max > min, this equals min
        @SerialName("adult") val isAdult: Boolean = true,
        @SerialName("death-commands") val deathCommands: List<String> = ArrayList(),
        val drops: List<MobDrop> = listOf()) {

    val name by lazy {
        _name ?: (mobzy.mobzyTypes.templates.entries.find { (_, template) -> template === this }
                ?: error("Template was accessed but not registered in any mob configuration")).key
    }

    fun chooseDrops(looting: Int = 0, fire: Int = 0): List<ItemStack?> = drops.toList().map { it.chooseDrop(looting, fire) }

    val modelItemStack
        get() = ItemStack(modelMaterial).editItemMeta {
            setCustomModelData(model)
        }
}