/*
package com.offz.spigot.mobzy.pathfinders

import com.offz.spigot.mobzy.mobs.CustomMob

class PathfinderGoalLookWhereHeaded(mob: CustomMob) : MobzyPathfinderGoal(mob) {
    override fun execute() {
        val controllermove = mob.controllerMove
        val x = controllermove.d() - mob.locX
        //        double y = controllermove.e() - mob.locY;
        val z = controllermove.f() - mob.locZ
        //look at where we're going
        val deltaX = x - mob.locX
        val deltaZ = z - mob.locZ

    }
    override fun shouldExecute(): Boolean {
        return !nmsEntity.controllerMove.b()
    }
}
*/
