package com.mineinabyss.mobzy.pathfinders.flying

import com.mineinabyss.mobzy.mobs.types.FlyingMob
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.random.Random

open class PathfinderGoalIdleFly(override val mob: FlyingMob) : MobzyPathfinderGoal() {
    protected var targetLoc: Location? = null

    //if there isn't an operation to move somewhere, we can start looking for somewhere to fly
    override fun shouldExecute(): Boolean = target == null

    override fun shouldKeepExecuting(): Boolean {
        val targetLoc = targetLoc ?: return false
        val dist = mob.distanceTo(targetLoc)
        return dist in 1.0..60.0 && entity.velocity != Vector(0, 0, 0) && target == null
    }

    override fun init() {
        val x = mob.locX + Random.nextDouble(-16.0, 16.0)
        val y = mob.locY + Random.nextDouble(-16.0, 12.0) //make it more likely to fly down
        val z = mob.locZ + Random.nextDouble(-16.0, 16.0)
        val loc = Location(entity.world, x, y, z)
        if (!loc.block.isPassable)
            return
        if (y > 16) { //keep mobs from going down and killing themselves
            targetLoc = loc
            moveController.a(x, y, z, 1.0) //TODO make a wrapper for the controller and figure out the difference between it and navigation
        }
    }

    override fun execute() {
        val targetLoc = targetLoc ?: return
        mob.lookAtPitchLock(targetLoc)
    }
}