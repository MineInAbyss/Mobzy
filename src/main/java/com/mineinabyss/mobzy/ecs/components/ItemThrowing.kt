package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.Serializable

@Serializable
class ItemThrowing(
        val itemToThrow: SerializableItemStack
)