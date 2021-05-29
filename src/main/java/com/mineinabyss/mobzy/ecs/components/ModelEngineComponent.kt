package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("geary:modelengine")
@AutoscanComponent
class ModelEngineComponent(
    val modelId: String,
//    val hitbox: Boolean = true,
    val invisible: Boolean = true,
    val rideable: Boolean = false,
    val damageTint: Boolean = true,
    val nametag: Boolean = true,
)
