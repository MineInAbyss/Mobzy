package com.offz.spigot.abyssalcreatures.mobs.hostile;

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour;
import com.offz.spigot.mobzy.mobs.types.HostileMob;
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.World;

public class Steve extends HostileMob implements HitBehaviour {
    public Steve(World world) {
        super(world, "Steve");
        setSize(2F, 7F);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, getStaticBuilder().getModelID()));
    }
}