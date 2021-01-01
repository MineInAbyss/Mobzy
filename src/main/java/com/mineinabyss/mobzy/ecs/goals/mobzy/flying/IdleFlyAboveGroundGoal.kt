package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.mobzy.api.pathfindergoals.moveTo
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.entity.Mob
import kotlin.random.Random

@Serializable
@SerialName("mobzy:behavior.idle_fly_above_ground")
class IdleFlyAboveGroundBehavior(
    private val maxHeight: Double = 4.0,
    private val radius: Double = 5.0
) : PathfinderComponent() {
    override fun build(mob: Mob) = IdleFlyAboveGroundGoal(
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
    override fun init() {
        val (x, y, z) = mob.location
        val dx = x + Random.nextDouble(-radius, radius)
        val dy = y + Random.nextDouble(-radius, radius / 2) //make it more likely to fly down
        val dz = z + Random.nextDouble(-radius, radius)
        val loc = Location(mob.world, dx, dy, dz)
        if (!loc.clone().add(0.0, -maxHeight, 0.0).block.type.isSolid) {
            moveController.moveTo(dx, dy - 0.1, dz, speed = 1.0)
            return
        }
        if (y > 16) { //keep mobs from going down and killing themselves
            targetLoc = loc
            //TODO make a wrapper for the controller and figure out the difference between it and navigation
            moveController.moveTo(x, y, z, 1.0)
        }
    }
}
