package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R2.PathfinderGoalLeapAtTarget
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.leap_at_target")
class LeapAtTargetBehavior(
    val jumpHeight: Float = 0.6f
) : PathfinderComponent() {
    override fun build(mob: Mob) = PathfinderGoalLeapAtTarget(mob.toNMS(), jumpHeight)
}
