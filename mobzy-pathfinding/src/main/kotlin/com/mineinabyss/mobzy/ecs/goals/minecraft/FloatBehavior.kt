package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.FloatGoal
import org.bukkit.entity.Creature

@Serializable
@SerialName("minecraft:behavior.float")
class FloatBehavior : PathfinderComponent() {
    override fun build(mob: Creature) = FloatGoal(mob.toNMS())
}
