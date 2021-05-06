package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.NMSPathfinderGoal
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R3.EntityCreature
import net.minecraft.server.v1_16_R3.PathfinderGoalRandomStrollLand
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.random_stroll_land")
class RandomStrollLandBehavior(
    private val speedModifier: Double = 1.0,
    private val frequency: Float = 0.001f
) : PathfinderComponent() {
    override fun build(mob: Mob): NMSPathfinderGoal = PathfinderGoalRandomStrollLand(
        mob.toNMS<EntityCreature>(),
        speedModifier,
        frequency
    )
}
