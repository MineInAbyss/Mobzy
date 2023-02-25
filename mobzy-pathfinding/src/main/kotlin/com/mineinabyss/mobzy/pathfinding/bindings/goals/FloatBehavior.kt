package com.mineinabyss.mobzy.pathfinding.bindings.goals

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.FloatGoal
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.float")
class FloatBehavior : PathfinderComponent() {
    override fun build(mob: Mob) = FloatGoal(mob.toNMS())
}
