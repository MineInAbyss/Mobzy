package com.mineinabyss.mobzy.pathfinding.bindings.goals

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.leap_at_target")
class LeapAtTargetBehavior(
    val jumpHeight: Float = 0.6f
) : PathfinderComponent() {
    override fun build(mob: Mob) = LeapAtTargetGoal(mob.toNMS(), jumpHeight)
}
