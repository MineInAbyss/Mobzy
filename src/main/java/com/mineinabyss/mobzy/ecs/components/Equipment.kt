package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.Serializable

@Serializable
class Equipment(
        val helmet: SerializableItemStack? = null,
        val chestplate: SerializableItemStack? = null,
        val leggings: SerializableItemStack? = null,
        val boots: SerializableItemStack? = null
) : MobzyComponent