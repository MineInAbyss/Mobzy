package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.idofront.nms.aliases.NMSCreatureType
import com.mineinabyss.mobzy.ecs.components.MobCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:type")
class MobzyType(
    val baseClass: String,
    val creatureType: NMSCreatureType,
    val mobCategory: MobCategory? = null
)
