package com.offz.spigot.custommobs.Pathfinders.Flying;

import com.offz.spigot.custommobs.Mobs.Flying.FlyingMob;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.MathHelper;
import net.minecraft.server.v1_13_R2.PathfinderGoal;

public class PathfinderGoalDiveOnTargetAttack extends PathfinderGoal {
    private final FlyingMob mob;

    public PathfinderGoalDiveOnTargetAttack(FlyingMob entityghast) {
        this.mob = entityghast;
        this.a(2);
    }

    public boolean a() {
        return true;
    }

    public void e() {
        if (this.mob.getGoalTarget() == null) {
            this.mob.yaw = -((float) MathHelper.c(this.mob.motX, this.mob.motZ)) * 57.295776F;
            this.mob.aQ = this.mob.yaw;
        } else {
            EntityLiving entityliving = this.mob.getGoalTarget();
            if (entityliving.h(this.mob) < 4096.0D) {
                double d1 = entityliving.locX - this.mob.locX;
                double d2 = entityliving.locZ - this.mob.locZ;
                this.mob.yaw = -((float) MathHelper.c(d1, d2)) * 57.295776F;
                this.mob.aQ = this.mob.yaw;

                Entity target = this.mob.getGoalTarget();
                this.mob.getControllerMove().a(target.locX, target.locY + 15 * Math.random(), target.locZ, 1.0D);

            }
        }

    }
}