package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.idofront.nms.aliases.NMSCreatureType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:type")
@AutoscanComponent
class MobzyTypeInjectionComponent(
    val baseClass: String,
    val creatureType: NMSCreatureType,
)
