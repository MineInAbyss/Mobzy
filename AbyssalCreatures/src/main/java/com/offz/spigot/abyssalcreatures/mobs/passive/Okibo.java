/*
package com.offz.spigot.abyssalcreatures.mobs.passive;

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour;
import com.offz.spigot.mobzy.mobs.types.PassiveMob;
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_15_R1.EntityAgeable;
import net.minecraft.server.v1_15_R1.EntityHuman;
import net.minecraft.server.v1_15_R1.World;

public class Okibo extends PassiveMob implements HitBehaviour {
    //TODO change offset when riding and make controllable
    public Okibo(World world) {
        super(world, "Okibo");
        setSize(3, 3);
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
        return new Okibo(this.world);
    }
}
*/
