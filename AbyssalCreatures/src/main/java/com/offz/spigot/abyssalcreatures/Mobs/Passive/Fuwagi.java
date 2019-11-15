package com.offz.spigot.abyssalcreatures.Mobs.Passive;

import com.offz.spigot.mobzy.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.mobzy.Mobs.Types.PassiveMob;
import com.offz.spigot.mobzy.Pathfinders.PathfinderGoalTemptPitchLock;
import com.offz.spigot.mobzy.Pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.EntityAgeable;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.PathfinderGoalAvoidTarget;
import net.minecraft.server.v1_13_R2.World;

public class Fuwagi extends PassiveMob implements HitBehaviour {
    public Fuwagi(World world) {
        super(world, "Fuwagi");
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, getStaticBuilder().getModelID()));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityHuman.class, 8.0F, 1D, 1D));
        goalSelector.a(4, new PathfinderGoalTemptPitchLock(this, 1.2D, false, getStaticBuilder().getTemptItems()));
    }

    @Override
    public String soundHurt() {
        return "entity.rabbit.attack";
    }

    @Override
    public String soundAmbient() {
        return "entity.rabbit.ambient";
    }

    @Override
    public String soundStep() {
        return "entity.rabbit.jump";
    }

    @Override
    public String soundDeath() {
        return "entity.rabbit.death";
    }

    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Fuwagi(this.world);
    }
}
