package com.offz.spigot.custommobs.Mobs.Passive;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalTemptPitchLock;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.*;

public class Fuwagi extends PassiveMob {
    static MobBuilder builder = new MobBuilder("Fuwagi", 5)
            .setTemptItems(new Item[]{Items.CARROT, Items.APPLE});

    public Fuwagi(World world) {
        super(world, builder);
    }

    @Override
    protected void createPathfinders() {
        super.createPathfinders();
        this.goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, builder.getModelID()));
        this.goalSelector.a(4, new PathfinderGoalTemptPitchLock(this, 1.2D, false, builder.getTemptItems()));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.2D);
    }

    public MobBuilder getBuilder() {
        return builder;
    }

    @Override
    protected MinecraftKey getDefaultLootTable() {
        return new MinecraftKey("entities/pig");
    }

    @Override
    protected SoundEffect soundHurt() {
        return SoundEffects.ENTITY_GENERIC_EXPLODE;
    }

    //creating children
    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Neritantan(this.world);
    }
}
