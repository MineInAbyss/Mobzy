package com.mineinabyss.mobzy.modelengine.riding

import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.ticxo.modelengine.api.mount.controller.MountControllerType
import com.ticxo.modelengine.api.mount.controller.MountControllerTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Allows nice client-side rendering when riding entities with a ModelEngine model set.
 */
@Serializable
@SerialName("mobzy:modelengine.rideable")
class Rideable(
    val controllerID: String = "WALKING",
    val steerItem: SerializableItemStack? = null,
    val canTakePassengers: Boolean = false,
    val maxPassengerCount: Int = 1,
    val canDamageMount: Boolean = false
) {
    val controller: MountControllerType
        get() =
            when (controllerID.uppercase()) {
                "WALKING" -> MountControllerTypes.WALKING
                "WALKING_FORCE" -> MountControllerTypes.WALKING_FORCE
                "FLYING" -> MountControllerTypes.FLYING
                "FLYING_FORCE" -> MountControllerTypes.FLYING_FORCE
                else -> MountControllerTypes.WALKING
            }
}
