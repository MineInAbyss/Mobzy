package com.mineinabyss.mobzy.ecs.pathfinders

import com.mineinabyss.mobzy.api.nms.goalwrappers.LookAtPlayerGoal
import com.mineinabyss.mobzy.ecs.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.look_at_player")
data class LookAtPlayerBehavior(
        val radius: Float,
        val startChance: Float = 0.02f
) : PathfinderComponent {
    override fun build(mob: Mob) = LookAtPlayerGoal(mob, radius, startChance)
}