package com.offz.spigot.custommobs.Pathfinders.Flying;

import com.offz.spigot.custommobs.Mobs.Flying.FlyingMob;

import java.util.Random;

public class PathfinderGoalIdleFlyAboveGround extends PathfinderGoalIdleFly {
    private double maxHeight;

    public PathfinderGoalIdleFlyAboveGround(FlyingMob flyingMob, int maxHeight) {
        super(flyingMob);
        this.maxHeight = maxHeight;
    }

    @Override
    public void c() {
        Random random = mob.getRandom();
        double dx = mob.locX + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        double dy = mob.locY + (double) ((random.nextFloat() * 2.0F - 1.5F) * 16.0F); //make it more likely to fly down
        double dz = mob.locZ + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        if (!mob.getBukkitEntity().getWorld().getBlockAt((int) dx, (int) (dy - maxHeight), (int) dz).getType().equals(org.bukkit.Material.AIR))
            mob.getControllerMove().a(dx, dy, dz, 1.0D);
    }
}
