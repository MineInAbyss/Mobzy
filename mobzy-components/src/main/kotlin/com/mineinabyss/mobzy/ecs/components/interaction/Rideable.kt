package com.mineinabyss.mobzy.ecs.components.interaction

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Allows players to right click on a mob with this component to start riding them.
 */
@Serializable
@SerialName("mobzy:rideable")
class Rideable(
    val requiresItemToSteer: Boolean = false,
    val steerItem: SerializableItemStack? = null,
    var isSaddled: Boolean = false,
    val canTakePassenger: Boolean = false,
    val maxPassengerCount: Int = 1,
)
