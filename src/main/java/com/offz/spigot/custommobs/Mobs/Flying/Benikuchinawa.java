package com.offz.spigot.custommobs.Mobs.Flying;

import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.Types.FlyingMob;
import net.minecraft.server.v1_13_R2.SoundEffect;
import net.minecraft.server.v1_13_R2.SoundEffects;
import net.minecraft.server.v1_13_R2.World;

public class Benikuchinawa extends FlyingMob implements HitBehaviour {
    public Benikuchinawa(World world) {
        super(world, "Benikuchinawa");
        setSize(5f, 2f);
        //An alternative method for setting boundaries which doesn't seem to work anymore
//        AxisAlignedBB boundingBox = new AxisAlignedBB(locX - 5, locY, locZ - 1, locX + 5, locY + 1, locZ + 1);
//        a(boundingBox);
    }

    @Override
    public SoundEffect soundAmbient() {
        return SoundEffects.ENTITY_LLAMA_AMBIENT;
    }

    @Override
    public SoundEffect soundDeath() {
        return SoundEffects.ENTITY_LLAMA_DEATH;
    }

    @Override
    public SoundEffect soundHurt() {
        return SoundEffects.ENTITY_LLAMA_HURT;
    }

    /*@Override
    public SoundEffect soundAmbient() {
        return SoundEffects.;
    }

    @Override
    public SoundEffect soundDeath() {
        return SoundEffects.;
    }

    @Override
    public SoundEffect soundHurt() {
        return SoundEffects.;
    }

    @Override
    public SoundEffect soundStep() {
        return SoundEffects.;
    }*/
}