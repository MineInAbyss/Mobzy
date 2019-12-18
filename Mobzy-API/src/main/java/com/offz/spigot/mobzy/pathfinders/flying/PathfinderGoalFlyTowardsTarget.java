/*
package com.offz.spigot.mobzy.pathfinders.flying;

import com.offz.spigot.mobzy.mobs.types.FlyingMob;
import net.minecraft.server.v1_15_R1.MathHelper;
import net.minecraft.server.v1_15_R1.PathfinderGoal;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

*/
/**
 * Looking at target based off EntityGhast's pathfinders
 *//*

public class PathfinderGoalFlyTowardsTarget extends PathfinderGoal {
    private FlyingMob mob;

    public PathfinderGoalFlyTowardsTarget(FlyingMob flyingMob) {
        mob = flyingMob;
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
            LivingEntity target = (LivingEntity) this.mob.getGoalTarget().getBukkitEntity();
            mob.lookAt(target);
            Location targetLoc = target.getLocation();
            //move towards target
            this.mob.getControllerMove().a(targetLoc.getX(), targetLoc.getY(), targetLoc.getZ(), 1);
            if (targetLoc.getY() > mob.getY())
                this.mob.getControllerMove().a(targetLoc.getX(), mob.getY() + 1, targetLoc.getZ(), 4);

//            if (targetLoc.getY() > mob.locY)
//                this.mob.getControllerMove().a(targetLoc.getX(), mob.locY + 1, targetLoc.getZ(), 4);
//            else
//                this.mob.getControllerMove().a(targetLoc.getX(), mob.locY - 1, targetLoc.getZ(), 4);
        }
    }
}*/
