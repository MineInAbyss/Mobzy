package com.offz.spigot.abyssialcreatures.Mobs.Passive;

import com.offz.spigot.mobzy.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.mobzy.Mobs.Types.PassiveMob;
import com.offz.spigot.mobzy.Pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.EntityAgeable;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.World;

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
        this.goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, getStaticBuilder().getModelID()));
    }

    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Okibo(this.world);
    }
}
