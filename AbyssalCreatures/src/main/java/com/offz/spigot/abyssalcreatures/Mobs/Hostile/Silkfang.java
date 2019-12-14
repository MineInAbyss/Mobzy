package com.offz.spigot.abyssalcreatures.Mobs.Hostile;

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour;
import com.offz.spigot.mobzy.mobs.types.HostileMob;
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.World;

public class Silkfang extends HostileMob implements HitBehaviour {
    public Silkfang(World world) {
        super(world, "Silkfang");
        this.setSize(2F, 2F);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, getStaticBuilder().getModelID()));
    }
}