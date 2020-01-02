package com.offz.spigot.mobzy.pathfinders.flying

import com.offz.spigot.mobzy.mobs.types.FlyingMob
import org.bukkit.Location
import kotlin.random.Random

class PathfinderGoalIdleFlyAboveGround(mob: FlyingMob, private val maxHeight: Double = 4.0, private val radius: Double = 5.0) : PathfinderGoalIdleFly(mob) {

    override fun init() {
        val x = mob.x + Random.nextDouble(-radius, radius)
        val y = mob.y + Random.nextDouble(-radius / 4, radius) //make it more likely to fly down
        val z = mob.z + Random.nextDouble(-radius, radius)
        val loc = Location(entity.world, x, y, z)
        if (!loc.clone().add(0.0, -maxHeight, 0.0).block.type.isSolid) {
            moveController.a(mob.x, mob.y - 0.1, mob.z, 1.0)
            return
        }
        if (y > 16) { //keep mobs from going down and killing themselves
            targetLoc = loc
            moveController.a(x, y, z, 1.0) //TODO make a wrapper for the controller and figure out the difference between it and navigation
        }
    }
}