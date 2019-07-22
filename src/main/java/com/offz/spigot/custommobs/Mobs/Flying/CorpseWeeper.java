package com.offz.spigot.custommobs.Mobs.Flying;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.Types.FlyingMob;
import net.minecraft.server.v1_13_R2.SoundEffect;
import net.minecraft.server.v1_13_R2.SoundEffects;
import net.minecraft.server.v1_13_R2.World;

public class CorpseWeeper extends FlyingMob implements HitBehaviour {
    static MobBuilder builder = new MobBuilder("Corpse Weeper", 11);

    public CorpseWeeper(World world) {
        super(world, builder);
        setSize(3f, 3f);
    }

    @Override
    public SoundEffect soundAmbient() {
        return SoundEffects.ENTITY_BAT_AMBIENT;
    }

    @Override
    public SoundEffect soundDeath() {
        return SoundEffects.ENTITY_BAT_DEATH;
    }

    @Override
    public SoundEffect soundHurt() {
        return SoundEffects.ENTITY_BAT_HURT;
    }
}