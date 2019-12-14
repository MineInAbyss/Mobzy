package com.offz.spigot.abyssalcreatures.Mobs.Hostile;

import com.offz.spigot.abyssalcreatures.Mobs.Passive.Neritantan;
import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour;
import com.offz.spigot.mobzy.mobs.types.HostileMob;
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_13_R2.World;

public class Inbyo extends HostileMob implements HitBehaviour {
    public Inbyo(World world) {
        super(world, "Inbyo");
        this.setSize(0.6F, 3F);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, getStaticBuilder().getModelID()));
        targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, Neritantan.class, true));
    }
}