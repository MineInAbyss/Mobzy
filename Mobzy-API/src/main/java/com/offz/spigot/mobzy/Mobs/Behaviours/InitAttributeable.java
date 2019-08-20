package com.offz.spigot.mobzy.Mobs.Behaviours;

import com.offz.spigot.mobzy.Builders.MobBuilder;
import com.offz.spigot.mobzy.Mobs.CustomMob;
import com.offz.spigot.mobzy.Mobs.Types.FlyingMob;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.GenericAttributes;

public class InitAttributeable extends MobBehaviour {
    public InitAttributeable(CustomMob mob) {
        super(mob);
    }

    public void setConfiguredAttributes() {
        EntityLiving entity = mob.getEntity();
        MobBuilder builder = mob.getStaticBuilder();

        if (builder == null)
            return;

        if (builder.getMaxHealth() != null)
            entity.getAttributeInstance(GenericAttributes.maxHealth).setValue(builder.getMaxHealth());
        if (builder.getMovementSpeed() != null)
            entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(builder.getMovementSpeed());
        if (builder.getAttackDamage() != null && !(mob instanceof FlyingMob)) //flying mobs can't have an attack damage attribute, we use the builder's value instead
            entity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(builder.getAttackDamage());
        if (builder.getFollowRange() != null)
            entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(builder.getFollowRange());
    }
}
