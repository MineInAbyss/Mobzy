package com.mineinabyss.mobzy.ecs.behaviors.combat

import com.mineinabyss.mobzy.api.nms.goalwrappers.HurtByTargetGoal
import com.mineinabyss.mobzy.ecs.components.PathfinderComponent
import com.mineinabyss.mobzy.mobs.CustomMob
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Creature

@Serializable
@SerialName("minecraft:behavior.hurt_by_target")
class HurtByTargetBehavior: PathfinderComponent {
    override fun createPathfinder(mob: CustomMob<*>)
            = HurtByTargetGoal(mob.entity as? Creature ?: error("Mob bust be a creature"))
}