package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A component for adding equipment to spawned mobs.
 */
@Serializable
@SerialName("mobzy:equipment")
data class Equipment(
    val helmet: SerializableItemStack? = null,
    val chestplate: SerializableItemStack? = null,
    val leggings: SerializableItemStack? = null,
    val boots: SerializableItemStack? = null
)
