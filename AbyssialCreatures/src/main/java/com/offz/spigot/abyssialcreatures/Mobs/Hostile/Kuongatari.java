package com.offz.spigot.abyssialcreatures.Mobs.Hostile;

import com.offz.spigot.mobzy.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.mobzy.Mobs.Types.HostileMob;
import com.offz.spigot.mobzy.Pathfinders.PathfinderGoalWalkingAnimation;
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
        this.goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, getStaticBuilder().getModelID()));
        this.goalSelector.a(1, new PathfinderGoalLeapAtTarget(this, 0.6F));
    }
}