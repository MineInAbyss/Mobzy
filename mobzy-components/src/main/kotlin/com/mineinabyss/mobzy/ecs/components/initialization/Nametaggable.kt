package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:nametaggable")
@AutoscanComponent
data class Nametaggable(
    val nametaggable: Boolean = true,
    val tagOffset: Double = 20.0
)