package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.MobzyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:sounds")
class Sounds(
        val ambient: String? = null,
        val death: String? = null,
        val hurt: String? = null,
        val step: String? = null
) : MobzyComponent