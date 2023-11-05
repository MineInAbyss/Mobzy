package com.mineinabyss.mobzy.features.bucketable

import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
@SerialName("mobzy:bucketable")
class Bucketable(
    val bucketLiquidRequired: Material = Material.WATER,
    val bucketItem: SerializableItemStack
)
