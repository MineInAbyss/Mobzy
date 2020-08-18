package com.mineinabyss.mobzy.ecs.pathfinders.combat

import com.mineinabyss.mobzy.api.nms.goalwrappers.HurtByTargetGoal
import com.mineinabyss.mobzy.ecs.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Creature
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.hurt_by_target")
class HurtByTargetBehavior : PathfinderComponent {
    override fun createPathfinder(mob: Mob) =
            HurtByTargetGoal(mob as? Creature ?: error("Mob bust be a creature"))
}