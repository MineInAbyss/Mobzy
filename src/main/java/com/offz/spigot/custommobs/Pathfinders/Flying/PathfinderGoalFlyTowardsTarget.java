package com.offz.spigot.custommobs.Pathfinders.Flying;

import com.offz.spigot.custommobs.Mobs.Types.FlyingMob;
import net.minecraft.server.v1_13_R2.DamageSource;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.MathHelper;
import net.minecraft.server.v1_13_R2.PathfinderGoal;

/**
 * Looking at target based off EntityGhast's pathfinders
 */
public class PathfinderGoalFlyTowardsTarget extends PathfinderGoal {
    private FlyingMob mob;

    public PathfinderGoalFlyTowardsTarget(FlyingMob flyingMob) {
        this.mob = flyingMob;
        this.a(2); //TODO no idea what this does
    }

    public boolean a() {
        return true;
    }

    public void e() {
        if (this.mob.getGoalTarget() == null) {
            this.mob.yaw = -((float) MathHelper.c(this.mob.motX, this.mob.motZ)) * 57.295776F;
            this.mob.aQ = this.mob.yaw;
        } else {
            //look at target
            EntityLiving target = this.mob.getGoalTarget();
            double d1 = target.locX - this.mob.locX;
            double d2 = target.locZ - this.mob.locZ;
            this.mob.yaw = -((float) MathHelper.c(d1, d2)) * 57.295776F;
            this.mob.aQ = this.mob.yaw;

            //move towards target
            this.mob.getControllerMove().a(target.locX, target.locY, target.locZ, 1);
            if (target.locY > mob.locY)
                this.mob.getControllerMove().a(mob.locX, mob.locY + 1, mob.locZ, 4);

            //if within range, harm
            if (mob.h(target) < 1.8)
                target.damageEntity(DamageSource.mobAttack(mob), 1);

        }
    }
}