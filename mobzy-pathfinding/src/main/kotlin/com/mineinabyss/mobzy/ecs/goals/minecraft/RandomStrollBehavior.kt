package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.idofront.time.inWholeTicks
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.RandomStrollGoal
import org.bukkit.entity.Mob
import kotlin.time.Duration

@Serializable
@SerialName("minecraft:behavior.random_stroll")
class RandomStrollBehavior(
    private val speedModifier: Double = 1.0,
    @Serializable(with = DurationSerializer::class)
    private val interval: Duration = 120.ticks
) : PathfinderComponent() {
    override fun build(mob: Mob) = RandomStrollGoal(
        mob.toNMS<PathfinderMob>(),
        speedModifier,
        interval.inWholeTicks.toInt()
    )
}
