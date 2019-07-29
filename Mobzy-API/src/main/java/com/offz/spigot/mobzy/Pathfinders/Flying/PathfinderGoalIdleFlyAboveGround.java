package com.offz.spigot.mobzy.Pathfinders.Flying;

import com.offz.spigot.mobzy.Mobs.Types.FlyingMob;

import java.util.Random;

public class PathfinderGoalIdleFlyAboveGround extends PathfinderGoalIdleFly {
    private double maxHeight;
    private double radius;

    public PathfinderGoalIdleFlyAboveGround(FlyingMob flyingMob, int maxHeight, double radius) {
        super(flyingMob);
        this.maxHeight = maxHeight;
        this.radius = radius;
    }

    @Override
    public void c() {
        Random random = mob.getRandom();
        double dx = mob.locX + ((random.nextFloat() * 2 - 1) * radius);
        double dy = mob.locY + ((random.nextFloat()) * maxHeight); //make it more likely to fly down
        double dz = mob.locZ + ((random.nextFloat() * 2 - 1) * radius);

        if (mob.getBukkitEntity().getWorld().getBlockAt((int) dx, (int) (dy - maxHeight), (int) dz).getType().isSolid())
            mob.getControllerMove().a(dx, dy, dz, 1.0D);
        else
            mob.getControllerMove().a(dx, mob.locY - maxHeight, dz, 1.0D);
    }
}
