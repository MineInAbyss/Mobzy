package com.offz.spigot.mobzy.Pathfinders.Flying;

import com.offz.spigot.mobzy.Mobs.Types.FlyingMob;
import net.minecraft.server.v1_13_R2.ControllerMove;
import net.minecraft.server.v1_13_R2.PathfinderGoal;

import java.util.Random;

/**
 * Most code from EntityGhast's pathfinders
 */
public class PathfinderGoalIdleFly extends PathfinderGoal {
    protected final FlyingMob mob;

    public PathfinderGoalIdleFly(FlyingMob flyingMob) {
        this.mob = flyingMob;
    }

    public boolean a() {
        ControllerMove controllermove = mob.getControllerMove();
        if (!controllermove.b()) {
            return true;
        } else {
            double d0 = controllermove.d() - mob.locX;
            double d1 = controllermove.e() - mob.locY;
            double d2 = controllermove.f() - mob.locZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0D || d3 > 3600.0D;
        }
    }

    public boolean b() {
        return false;
    }

    public void c() {
        Random random = this.mob.getRandom();
        double x = mob.locX + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        double y = mob.locY + (double) ((random.nextFloat() * 2.0F - 1.5F) * 16.0F); //make it more likely to fly down
        double z = mob.locZ + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        if (y > 16) { //keep mobs from going down and killing themselves
            this.mob.getControllerMove().a(x, y, z, 1.0D);
            mob.lookAt(x, y, z);
        }
    }
}