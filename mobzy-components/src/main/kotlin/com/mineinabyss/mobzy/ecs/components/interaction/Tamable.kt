package com.mineinabyss.mobzy.ecs.components.interaction

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:tamable")
class Tamable(
    val tameItem: SerializableItemStack? = null,
    val saddleModelId: String? = null,

)
