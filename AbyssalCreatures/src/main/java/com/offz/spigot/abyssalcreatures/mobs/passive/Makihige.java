package com.offz.spigot.abyssalcreatures.mobs.passive;

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour;
import com.offz.spigot.mobzy.mobs.types.PassiveMob;
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.EntityAgeable;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.PathfinderGoalAvoidTarget;
import net.minecraft.server.v1_13_R2.World;

public class Makihige extends PassiveMob implements HitBehaviour {
    public Makihige(World world) {
        super(world, "Makihige");
        setSize(2, 2);
    }

    @Override
    public void onRightClick(EntityHuman player) {
        player.startRiding(this);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, getStaticBuilder().getModelID()));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityHuman.class, 8.0F, 1D, 1D));
    }

    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Makihige(this.world);
    }
}
