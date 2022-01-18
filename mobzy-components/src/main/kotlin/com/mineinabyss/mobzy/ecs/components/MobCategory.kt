package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.minecraft.access.toGeary
import com.mineinabyss.idofront.nms.aliases.NMSCreatureType
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.nms.entity.creatureType
import com.mineinabyss.idofront.typealiases.BukkitEntity
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

val BukkitEntity.mobCategory
    get() = toGeary().get(MobCategory::class) ?: toNMS().entityType.creatureType.toMobCategory()
