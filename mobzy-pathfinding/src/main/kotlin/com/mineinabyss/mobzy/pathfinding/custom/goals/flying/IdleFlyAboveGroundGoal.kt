package com.mineinabyss.mobzy.pathfinding.custom.goals.flying

import com.mineinabyss.idofront.util.randomOrMin
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.Goal
import org.bukkit.Location
import org.bukkit.entity.Mob
import kotlin.random.Random

@Serializable
@SerialName("mobzy:behavior.idle_fly_above_ground")
class IdleFlyAboveGroundBehavior(
    private val maxHeight: Double = 4.0,
    private val radius: Double = 5.0
) : PathfinderComponent() {
    override fun build(mob: Mob): Goal = IdleFlyAboveGroundGoal(
        mob,
        maxHeight,
        radius
    )
}

class IdleFlyAboveGroundGoal(
    mob: Mob,
    private val maxHeight: Double = 4.0,
    private val radius: Double = 5.0
) : IdleFlyGoal(mob) {
    override fun findLoc(): Location? = mob.location.apply {
        x += (-radius..radius).randomOrMin()
        //make it more likely to fly down and impossible to fly into void
        y = (y + Random.nextDouble(-radius, radius / 2)).coerceAtLeast(1.0)
        z += (-radius..radius).randomOrMin()
    }.takeIf { it.clone().add(0.0, -maxHeight, 0.0).block.type.isSolid }
}
