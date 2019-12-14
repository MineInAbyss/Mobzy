package com.offz.spigot.abyssalcreatures.Mobs.Flying;

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour;
import com.offz.spigot.mobzy.mobs.types.FlyingMob;
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalFlyDamageTarget;
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalFlyTowardsTarget;
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalIdleFlyAboveGround;
import net.minecraft.server.v1_13_R2.PathfinderGoalFloat;
import net.minecraft.server.v1_13_R2.PathfinderGoalTargetNearestPlayer;
import net.minecraft.server.v1_13_R2.World;

public class Kazura extends FlyingMob implements HitBehaviour {
    public Kazura(World world) {
        super(world, "Kazura");
        setSize(1f, 1f);
    }

    @Override
    public void createPathfinders() {
        goalSelector.a(1, new PathfinderGoalFlyDamageTarget(this));
        goalSelector.a(3, new PathfinderGoalFlyTowardsTarget(this));
        goalSelector.a(7, new PathfinderGoalIdleFlyAboveGround(this, 2, 16));
        goalSelector.a(0, new PathfinderGoalFloat(this));
        targetSelector.a(1, new PathfinderGoalTargetNearestPlayer(this));
    }
}