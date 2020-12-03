package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.idofront.items.editItemMeta
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Serializable
@SerialName("mobzy:model")
data class Model(
        val id: Int,
        val material: Material = Material.DIAMOND_SWORD,
        val walkIdOffset: Int? = 1,
        val hitIdOffset: Int? = 2,
        val small: Boolean = false
) : GearyComponent() {
    val walkId = walkIdOffset?.plus(id)
    val hitId = hitIdOffset?.plus(id)
    val modelItemStack
        get() = ItemStack(material).editItemMeta {
            setCustomModelData(id)
        }
}