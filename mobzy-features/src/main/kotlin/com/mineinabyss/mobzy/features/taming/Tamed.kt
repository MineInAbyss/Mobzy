package com.mineinabyss.mobzy.features.taming

import com.mineinabyss.idofront.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@SerialName("mobzy:tamed")
class Tamed(
    @Serializable(with = UUIDSerializer::class)
    val owner: UUID,
)
