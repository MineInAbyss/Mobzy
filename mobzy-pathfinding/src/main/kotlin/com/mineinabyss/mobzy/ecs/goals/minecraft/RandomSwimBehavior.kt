package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.idofront.time.inWholeTicks
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal
import org.bukkit.entity.Creature
import kotlin.time.Duration

@Serializable
@SerialName("minecraft:behavior.random_swim")
class RandomSwimBehavior(
    private val speedModifier: Double = 1.0,
    @Serializable(with = DurationSerializer::class)
    private val interval: Duration = 120.ticks
) : PathfinderComponent() {
    override fun build(mob: Creature) = RandomSwimmingGoal(
        mob.toNMS(),
        speedModifier,
        interval.inWholeTicks.toInt()
    )
}
