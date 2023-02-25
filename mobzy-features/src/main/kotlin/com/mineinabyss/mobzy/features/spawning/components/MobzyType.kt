package com.mineinabyss.mobzy.features.spawning.components

import com.mineinabyss.geary.prefabs.PrefabKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:type")
class MobzyType(
    val baseClass: PrefabKey,
    @SerialName("creatureType")
    private val _creatureType: String = "MISC",
    val mobCategory: MobCategory? = null,
) {
    val creatureType get() = net.minecraft.world.entity.MobCategory.valueOf(_creatureType)
}
