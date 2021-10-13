package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.idofront.nms.aliases.NMSCreatureType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:mob_category")
enum class MobCategory {
    MONSTER,
    CREATURE,
    AMBIENT,
    WATER_CREATURE,
    WATER_AMBIENT,
    FLYING,
    MISC,
}

fun NMSCreatureType.toMobCategory(): MobCategory =
    MobCategory.valueOf(this.name)
