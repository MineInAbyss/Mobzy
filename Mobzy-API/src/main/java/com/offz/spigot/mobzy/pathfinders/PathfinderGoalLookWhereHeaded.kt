/*
package com.offz.spigot.mobzy.pathfinders

import com.github.ysl3000.bukkit.pathfinding.pathfinding.PathfinderGoal
import net.minecraft.server.v1_15_R1.EntityInsentient
import net.minecraft.server.v1_15_R1.MathHelper

*/
/**
 * Most code from EntityGhast's pathfinders
 *//*

class PathfinderGoalLookWhereHeaded(protected val mob: EntityInsentient) : PathfinderGoal {
    override fun execute() {
        val controllermove = mob.controllerMove
        val x = controllermove.d() - mob.locX
        //        double y = controllermove.e() - mob.locY;
        val z = controllermove.f() - mob.locZ
        //look at where we're going
        val deltaX = x - mob.locX
        val deltaZ = z - mob.locZ

    }
    override fun init() {}
    override fun reset() {}
    override fun shouldExecute(): Boolean {
        return !mob.controllerMove.b()
    }

    override fun shouldTerminate(): Boolean {
        return false
    }
}*/
