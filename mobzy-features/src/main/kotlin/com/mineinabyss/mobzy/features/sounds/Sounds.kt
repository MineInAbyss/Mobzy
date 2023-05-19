package com.mineinabyss.mobzy.features.sounds

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:sounds")
class Sounds(
    val ambient: String = "",
    val death: String = "",
    val hurt: String = "",
    val idle: String = "",
)
