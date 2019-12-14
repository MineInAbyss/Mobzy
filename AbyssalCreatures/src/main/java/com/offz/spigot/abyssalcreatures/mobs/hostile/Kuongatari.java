package com.offz.spigot.abyssalcreatures.mobs.hostile;

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour;
import com.offz.spigot.mobzy.mobs.types.HostileMob;
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.PathfinderGoalLeapAtTarget;
import net.minecraft.server.v1_13_R2.World;

public class Kuongatari extends HostileMob implements HitBehaviour {
    public Kuongatari(World world) {
        super(world, "Kuongatari");
        this.setSize(0.6F, 0.6F);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, getStaticBuilder().getModelID()));
        goalSelector.a(1, new PathfinderGoalLeapAtTarget(this, 0.6F));
    }
}