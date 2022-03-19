package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.idofront.util.randomOrMin
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.Goal
import org.bukkit.entity.Creature
import kotlin.random.Random

@Serializable
@SerialName("mobzy:behavior.idle_fly_above_ground")
class IdleFlyAboveGroundBehavior(
    private val maxHeight: Double = 4.0,
    private val radius: Double = 5.0
) : PathfinderComponent() {
    override fun build(mob: Creature): Goal = IdleFlyAboveGroundGoal(
        mob,
        maxHeight,
        radius
    )
}

class IdleFlyAboveGroundGoal(
    mob: Creature,
    private val maxHeight: Double = 4.0,
    private val radius: Double = 5.0
) : IdleFlyGoal(mob) {
    override fun init() {
        val targetLoc = mob.location.apply {
            x += (-radius..radius).randomOrMin()
            //make it more likely to fly down and impossible to fly into void
            y = (y + Random.nextDouble(-radius, radius / 2)).coerceAtLeast(1.0)
            z += (-radius..radius).randomOrMin()

        }

        // Only move if the block we aim at is solid
        if (targetLoc.clone().add(0.0, -maxHeight, 0.0).block.type.isSolid)
            pathfinder.moveTo(targetLoc, 1.0)
    }
}
