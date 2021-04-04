package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A component that allows mobs to move faster in water.
 *
 * Currently works by adding depth strider boots to the mob.
 *
 * @param level The enchantment level of depth strider to apply.
 */
@Serializable
@SerialName("mobzy:increased_water_speed")
@AutoscanComponent
data class IncreasedWaterSpeed(
    val level: Int = 10
)
