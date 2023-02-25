package com.mineinabyss.mobzy.modelengine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:modelengine")
class ModelEngineComponent(
    val modelId: String,
//    val hitbox: Boolean = true,
    val invisible: Boolean = true,
    val damageTint: Boolean = true,
    val nametag: Boolean = true,
    val leashable: Boolean = false,
    val stepHeight: Double = 0.0,
)
