package com.mineinabyss.mobzy.pathfinding.bindings.goals

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.FollowParentGoal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.animal.Animal
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.follow_parent")
class FollowParentBehaviour(
    private val speedModifier: Double = 1.0,
) : PathfinderComponent() {
    override fun build(mob: Mob): Goal = FollowParentGoal(
        mob.toNMS<Animal>(),
        speedModifier
    )
}
