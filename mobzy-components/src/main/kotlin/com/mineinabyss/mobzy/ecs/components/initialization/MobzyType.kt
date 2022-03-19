package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.mobzy.ecs.components.MobCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:type")
class MobzyType(
    val baseClass: String,
    val creatureType: net.minecraft.world.entity.MobCategory,
    val mobCategory: MobCategory? = null
)
