package com.mineinabyss.mobzy.ecs.components.interaction

import com.mineinabyss.idofront.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@SerialName("mobzy:tamed")
class Tamed(
    var owner: @Serializable(with = UUIDSerializer::class) UUID? = null,
)
