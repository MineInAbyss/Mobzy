package com.mineinabyss.mobzy.pathfinding.bindings.goals

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.random_look_around")
class RandomLookAroundBehavior : PathfinderComponent() {
    override fun build(mob: Mob) =
        RandomLookAroundGoal(mob.toNMS())
}
