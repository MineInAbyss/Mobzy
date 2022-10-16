package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.idofront.nms.aliases.toNMS
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Entity
import net.minecraft.world.entity.MobCategory as NMSMobCategory

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
    AXOLOTLS,
    UNDERGROUND_WATER_CREATURE
}

fun NMSMobCategory.toMobzyCategory(): MobCategory =
    MobCategory.valueOf(this.name)

val Entity.mobCategory
    get() = toGeary().get(MobCategory::class) ?: toNMS().type.category.toMobzyCategory()
