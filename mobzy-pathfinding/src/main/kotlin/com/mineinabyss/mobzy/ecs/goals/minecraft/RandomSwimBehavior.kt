package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.NMSEntityCreature
import com.mineinabyss.idofront.nms.aliases.NMSPathfinderGoal
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.time.TimeSpan
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomSwim
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.random_swim")
class RandomSwimBehavior(
    private val speedModifier: Double = 1.0,
    private val interval: TimeSpan = 120.ticks
) : PathfinderComponent() {
    override fun build(mob: Mob): NMSPathfinderGoal = PathfinderGoalRandomSwim(
        mob.toNMS<NMSEntityCreature>(),
        speedModifier,
        interval.inTicks.toInt()
    )
}