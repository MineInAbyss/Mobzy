package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.idofront.items.editItemMeta
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * A model to be used for custom mobs.
 *
 * @param id The customModelData for this item.
 * @param material The material for the model.
 * @param walkIdOffset An offset to apply when the entity is walking, null to ignore.
 * @param hitIdOffset An offset to apply when the entity is hit, null to ignore.
 * @param small Whether the entity should look like the baby version to clients.
 */
@Serializable
@SerialName("mobzy:model")
data class Model(
    val id: Int,
    val material: Material = Material.DIAMOND_SWORD,
    val walkIdOffset: Int? = 1,
    val hitIdOffset: Int? = 2,
    val small: Boolean = false
) : GearyComponent {
    val walkId = walkIdOffset?.plus(id)
    val hitId = hitIdOffset?.plus(id)
    val modelItemStack
        get() = ItemStack(material).editItemMeta {
            setCustomModelData(id)
        }
}
