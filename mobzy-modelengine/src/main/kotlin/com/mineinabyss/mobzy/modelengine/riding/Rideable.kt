package com.mineinabyss.mobzy.modelengine.riding

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Allows players to right-click on a mob with this component to start riding them.
 */
@Serializable
@SerialName("mobzy:modelengine.rideable")
class Rideable(
    val controllerID: String = "walking",
    val steerItem: SerializableItemStack? = null,
    val requiresItemToSteer: Boolean = steerItem != null,
    var isSaddled: Boolean = false,
    val canTakePassengers: Boolean = false,
    val maxPassengerCount: Int = 1,
    val canDamageMount: Boolean = false
)
