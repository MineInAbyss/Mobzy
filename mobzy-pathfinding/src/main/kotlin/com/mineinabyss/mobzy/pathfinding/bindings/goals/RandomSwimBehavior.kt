package com.mineinabyss.mobzy.pathfinding.bindings.goals

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.idofront.time.inWholeTicks
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal
import org.bukkit.entity.Mob
import kotlin.time.Duration

@Serializable
@SerialName("minecraft:behavior.random_swim")
class RandomSwimBehavior(
    private val speedModifier: Double = 1.0,
    @Serializable(with = DurationSerializer::class)
    private val interval: Duration = 120.ticks
) : PathfinderComponent() {
    override fun build(mob: Mob) = RandomSwimmingGoal(
        mob.toNMS<PathfinderMob>(),
        speedModifier,
        interval.inWholeTicks.toInt()
    )
}
