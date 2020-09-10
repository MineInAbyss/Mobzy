package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.MobzyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("debug:screaming")
data class Screaming (
        val scream: String = "AAAAAAAAAAAA"
):  MobzyComponent()