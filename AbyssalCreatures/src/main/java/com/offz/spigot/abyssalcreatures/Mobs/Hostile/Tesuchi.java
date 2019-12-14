package com.offz.spigot.abyssalcreatures.Mobs.Hostile;

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour;
import com.offz.spigot.mobzy.mobs.types.HostileMob;
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.World;

public class Tesuchi extends HostileMob implements HitBehaviour {
    public Tesuchi(World world) {
        super(world, "Tesuchi");
        this.setSize(0.6F, 0.6F);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, getStaticBuilder().getModelID()));
    }
}