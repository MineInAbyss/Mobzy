package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.mobzy.mobs.MobType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Serializable
@SerialName("mobzy:model")
data class Model(
        val id: Int,
        val material: Material = Material.DIAMOND_SWORD,
        val walkIdOffset: Int? = 1,
        val hitIdOffset: Int? = 2  //TODO check this default value works
) : SerializableComponent {
    override val copy = { copy() }
    val walkId = walkIdOffset?.plus(id)
    val hitId = hitIdOffset?.plus(id)
    val modelItemStack
        get() = ItemStack(material).editItemMeta {
            setCustomModelData(id)
        }
}

val MobType.model get() = get<Model>()