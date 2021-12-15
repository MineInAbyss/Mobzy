package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:item_model")
data class ItemModel(
    val item: SerializableItemStack
)
