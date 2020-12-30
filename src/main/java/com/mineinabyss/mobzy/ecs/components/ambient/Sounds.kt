package com.mineinabyss.mobzy.ecs.components.ambient

import com.mineinabyss.geary.ecs.GearyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:sounds")
class Sounds(
        val volume: Float? = null,
        val pitch: Double = 1.0,
        val pitchRange: Double = 0.2,
        val ambient: String? = null,
        val death: String? = null,
        val hurt: String? = null,
        val splash: String? = null,
        val swim: String? = null,
) : GearyComponent
