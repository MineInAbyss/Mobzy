package com.offz.spigot.mobzy.pathfinders.flying

import com.offz.spigot.mobzy.mobs.types.FlyingMob
import com.offz.spigot.mobzy.pathfinders.MobzyPathfinderGoal

class PathfinderGoalFlyTowardsTarget(mob: FlyingMob) : MobzyPathfinderGoal(mob) {
    override fun shouldExecute(): Boolean = (target != null)

    override fun shouldKeepExecuting(): Boolean = target != null

    override fun execute() {
        val target = target ?: return
        mob.lookAtPitchLock(target)

        val targetLoc = target.location
        moveController.a(targetLoc.x, targetLoc.y, targetLoc.z, 1.0) //TODO change to wrapper

        //aim slightly higher when below target to fix getting stuck
        if (targetLoc.y > mob.y)
            moveController.a(targetLoc.x, mob.y + 1, targetLoc.z, 1.0)
    }
}