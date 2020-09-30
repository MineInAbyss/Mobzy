package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.GearyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("debug:screaming")
data class Screaming (
        val scream: String = "AAAAAAAAAAAA"
):  GearyComponent()