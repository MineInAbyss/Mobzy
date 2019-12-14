package com.offz.spigot.mobzy.pathfinders;

import net.minecraft.server.v1_13_R2.ControllerMove;
import net.minecraft.server.v1_13_R2.EntityInsentient;
import net.minecraft.server.v1_13_R2.MathHelper;
import net.minecraft.server.v1_13_R2.PathfinderGoal;

/**
 * Most code from EntityGhast's pathfinders
 */
public class PathfinderGoalLookWhereHeaded extends PathfinderGoal {
    protected final EntityInsentient mob;

    public PathfinderGoalLookWhereHeaded(EntityInsentient mob) {
        this.mob = mob;
    }

    public boolean a() {
        return !mob.getControllerMove().b();
    }

    public boolean b() {
        return false;
    }

    public void c() {
        ControllerMove controllermove = mob.getControllerMove();

        double x = controllermove.d() - mob.locX;
//        double y = controllermove.e() - mob.locY;
        double z = controllermove.f() - mob.locZ;

        //look at where we're going
        double deltaX = x - this.mob.locX;
        double deltaZ = z - this.mob.locZ;
        this.mob.yaw = -((float) MathHelper.c(deltaX, deltaZ)) * 57.295776F;
        this.mob.aQ = this.mob.yaw;
    }
}