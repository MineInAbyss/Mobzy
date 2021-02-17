package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.mobzy.api.nms.aliases.NMSCreatureType
import kotlinx.serialization.Serializable

@Serializable
class MobzySpawnComponent(
    val baseClass: String,
    val creatureType: NMSCreatureType,
)
