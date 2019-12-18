/*
package com.offz.spigot.mobzy.pathfinders.controllers;

import com.offz.spigot.mobzy.mobs.types.FlyingMob;
import net.minecraft.server.v1_15_R1.AxisAlignedBB;
import net.minecraft.server.v1_15_R1.ControllerMove;
import net.minecraft.server.v1_15_R1.MathHelper;

public class MZControllerMoveFlying extends ControllerMove {
    private final FlyingMob mob;
    private int j;

    public MZControllerMoveFlying(FlyingMob flyingMob) {
        super(flyingMob);
        this.mob = flyingMob;
    }

    public void a() {
        if (this.h == Operation.MOVE_TO) {
            double dx = this.b - this.mob.locX;
            double dy = this.c - this.mob.locY;
            double dz = this.d - this.mob.locZ;
            double distance = dx * dx + dy * dy + dz * dz;
            if (this.j-- <= 0) {
                this.j += this.mob.getRandom().nextInt(5) + 2;
                distance = (double) MathHelper.sqrt(distance);
//                if (this.hasLineOfSight(this.b, this.c, this.d, distance)) {
                    Double speed = mob.getStaticBuilder().getMovementSpeed();
                    if (speed == null)
                        speed = 0.1;
                    mob.motX += dx / distance * speed;
                    mob.motY += dy / distance * speed;
                    mob.motZ += dz / distance * speed;
//                } else {
//                    this.h = Operation.WAIT;
//                }
            }
        }
    }

    private boolean hasLineOfSight(double dx, double dy, double dz, double distance) {
        double x = (dx - this.mob.locX) / distance;
        double y = (dy - this.mob.locY) / distance;
        double z = (dz - this.mob.locZ) / distance;
        AxisAlignedBB axisalignedbb = this.mob.getBoundingBox();

        for (int i = 1; (double) i < distance; ++i) {
            axisalignedbb = axisalignedbb.d(x, y, z);
            if (!this.mob.world.getCubes(this.mob, axisalignedbb)) {
                return false;
            }
        }

        return true;
    }
}*/
