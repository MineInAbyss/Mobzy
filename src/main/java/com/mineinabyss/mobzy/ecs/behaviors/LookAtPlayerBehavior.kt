package com.mineinabyss.mobzy.ecs.behaviors

import com.mineinabyss.mobzy.api.nms.goalwrappers.LookAtPlayerGoal
import com.mineinabyss.mobzy.ecs.components.PathfinderComponent
import com.mineinabyss.mobzy.mobs.CustomMob
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:behavior.look_at_player")
data class LookAtPlayerBehavior(
        val radius: Float,
        val startChance: Float = 0.02f
) : PathfinderComponent {
    override fun createPathfinder(mob: CustomMob<*>) = LookAtPlayerGoal(mob.entity, radius, startChance)
}