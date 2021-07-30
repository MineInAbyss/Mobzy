package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.float")
class FloatBehavior : PathfinderComponent() {
    override fun build(mob: Mob) = PathfinderGoalFloat(mob.toNMS())
}
