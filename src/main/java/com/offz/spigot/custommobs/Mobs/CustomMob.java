package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import net.minecraft.server.v1_13_R2.SoundEffect;
import net.minecraft.server.v1_13_R2.SoundEffects;

public interface CustomMob {
    MobBuilder getBuilder();

    default SoundEffect soundAmbient() {
        return SoundEffects.ENTITY_PIG_AMBIENT;
    }

    default SoundEffect soundHurt() {
        return SoundEffects.ENTITY_PIG_HURT;
    }

    default SoundEffect soundDeath() {
        return SoundEffects.ENTITY_PIG_DEATH;
    }

    default SoundEffect soundStep() {
        return SoundEffects.ENTITY_PIG_STEP;
    }
}