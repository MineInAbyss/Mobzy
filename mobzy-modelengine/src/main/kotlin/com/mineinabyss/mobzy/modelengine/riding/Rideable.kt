package com.mineinabyss.mobzy.modelengine.riding

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Allows nice client-side rendering when riding entities with a ModelEngine model set.
 */
@Serializable
@SerialName("mobzy:modelengine.rideable")
class Rideable(
    val controllerID: String = "walking",
    val steerItem: SerializableItemStack? = null,
    val canTakePassengers: Boolean = false,
    val maxPassengerCount: Int = 1,
    val canDamageMount: Boolean = false
)
