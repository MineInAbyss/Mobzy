package com.offz.spigot.custommobs.Mobs;

import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_13_R2.World;

public class Inbyo extends CustomZombie {
    public Inbyo(World world) {
        super(world);
        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<>(this, Neritantan.class, true));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(40.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.45);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(7.0D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(64.0D);
    }
}
