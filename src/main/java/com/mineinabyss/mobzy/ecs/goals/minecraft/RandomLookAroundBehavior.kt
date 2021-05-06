package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.NMSPathfinderGoal
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R3.PathfinderGoalRandomLookaround
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.random_look_around")
class RandomLookAroundBehavior : PathfinderComponent() {
    override fun build(mob: Mob): NMSPathfinderGoal =
        PathfinderGoalRandomLookaround(mob.toNMS())
}
