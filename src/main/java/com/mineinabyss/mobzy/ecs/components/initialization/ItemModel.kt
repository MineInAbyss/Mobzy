package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.autoscan.AutoscanComponent
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:item_model")
@AutoscanComponent
data class ItemModel(
    val item: SerializableItemStack
)