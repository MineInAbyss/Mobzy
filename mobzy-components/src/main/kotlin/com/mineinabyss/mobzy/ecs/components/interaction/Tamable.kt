package com.mineinabyss.mobzy.ecs.components.interaction

import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@SerialName("mobzy:tamable")
class Tamable(
    val isTamable: Boolean = true,
    val tameItem: SerializableItemStack? = null,
    var isTamed: Boolean = false,
    var owner: @Serializable(with = UUIDSerializer::class) UUID? = null,
)