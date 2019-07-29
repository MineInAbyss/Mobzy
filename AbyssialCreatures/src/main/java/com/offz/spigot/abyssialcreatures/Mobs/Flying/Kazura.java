package com.offz.spigot.abyssialcreatures.Mobs.Flying;

import com.offz.spigot.mobzy.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.mobzy.Mobs.Types.FlyingMob;
import com.offz.spigot.mobzy.Pathfinders.Flying.PathfinderGoalFlyTowardsTarget;
import com.offz.spigot.mobzy.Pathfinders.Flying.PathfinderGoalIdleFlyAboveGround;
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
        this.goalSelector.a(3, new PathfinderGoalFlyTowardsTarget(this));
        this.goalSelector.a(7, new PathfinderGoalIdleFlyAboveGround(this, 5, 16));
        this.goalSelector.a(0, new PathfinderGoalFloat(this));

        this.targetSelector.a(1, new PathfinderGoalTargetNearestPlayer(this));
    }
}