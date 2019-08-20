package com.offz.spigot.abyssialcreatures.Mobs.Passive;

import com.offz.spigot.mobzy.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.mobzy.Mobs.Types.PassiveMob;
import com.offz.spigot.mobzy.Pathfinders.PathfinderGoalTemptPitchLock;
import com.offz.spigot.mobzy.Pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.EntityAgeable;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.PathfinderGoalAvoidTarget;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Sound;

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
    public Sound soundHurt() {
        return Sound.ENTITY_RABBIT_ATTACK;
    }

    @Override
    public Sound soundAmbient() {
        return Sound.ENTITY_RABBIT_AMBIENT;
    }

    @Override
    public Sound soundStep() {
        return Sound.ENTITY_RABBIT_JUMP;
    }

    @Override
    public Sound soundDeath() {
        return Sound.ENTITY_RABBIT_DEATH;
    }

    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Fuwagi(this.world);
    }
}
