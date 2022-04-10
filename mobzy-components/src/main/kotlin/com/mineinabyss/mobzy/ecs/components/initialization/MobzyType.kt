package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.mobzy.ecs.components.MobCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:type")
class MobzyType(
    val baseClass: String,
    @SerialName("creatureType")
    private val _creatureType: String,
    val mobCategory: MobCategory? = null
) {
    val creatureType get() = net.minecraft.world.entity.MobCategory.byName(_creatureType)
}
