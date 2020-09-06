package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.geary.ecs.MobzyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:increased_water_speed")
data class IncreasedWaterSpeed(
        val level: Int = 10
): MobzyComponent