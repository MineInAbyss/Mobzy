package com.mineinabyss.mobzy.modelengine.intializers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:set.modelengine-model")
class SetModelEngineModel(
    val modelId: String,
//    val hitbox: Boolean = true,
    val invisible: Boolean = true,
    val damageTint: Boolean = true,
    val nametag: Boolean = true,
    val stepHeight: Double = 0.0,
)
