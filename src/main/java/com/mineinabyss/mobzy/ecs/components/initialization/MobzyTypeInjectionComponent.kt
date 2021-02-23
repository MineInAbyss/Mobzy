package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.mobzy.api.nms.aliases.NMSCreatureType
import kotlinx.serialization.Serializable

@Serializable
class MobzyTypeInjectionComponent(
    val baseClass: String,
    val creatureType: NMSCreatureType,
)
