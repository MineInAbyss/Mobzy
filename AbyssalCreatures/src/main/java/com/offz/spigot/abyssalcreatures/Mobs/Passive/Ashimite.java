package com.offz.spigot.abyssalcreatures.Mobs.Passive;

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour;
import com.offz.spigot.mobzy.mobs.types.PassiveMob;
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.EntityAgeable;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.World;

public class Ashimite extends PassiveMob implements HitBehaviour {
    public Ashimite(World world) {
        super(world, "Ashimite");
        setSize(2f, 2);
    }

    @Override
    public void onRightClick(EntityHuman player) {
        player.startRiding(this);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, getStaticBuilder().getModelID()));
    }

    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Ashimite(this.world);
    }
}
