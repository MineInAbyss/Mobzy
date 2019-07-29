package com.offz.spigot.abyssialcreatures.Mobs.Passive;

import com.offz.spigot.mobzy.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.mobzy.Mobs.Types.PassiveMob;
import com.offz.spigot.mobzy.Pathfinders.PathfinderGoalTemptPitchLock;
import com.offz.spigot.mobzy.Pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.*;

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
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.2D);
    }

    @Override
    public SoundEffect soundHurt() {
        return SoundEffects.ENTITY_RABBIT_ATTACK;
    }

    @Override
    public SoundEffect soundAmbient() {
        return SoundEffects.ENTITY_RABBIT_AMBIENT;
    }

    @Override
    public SoundEffect soundStep() {
        return SoundEffects.ENTITY_RABBIT_JUMP;
    }

    @Override
    public SoundEffect soundDeath() {
        return SoundEffects.ENTITY_RABBIT_DEATH;
    }

    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Fuwagi(this.world);
    }
}
