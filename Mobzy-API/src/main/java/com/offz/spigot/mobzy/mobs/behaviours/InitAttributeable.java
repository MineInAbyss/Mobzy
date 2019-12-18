package com.offz.spigot.mobzy.mobs.behaviours;

import com.offz.spigot.mobzy.mobs.CustomMob;
import com.offz.spigot.mobzy.mobs.MobTemplate;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.GenericAttributes;

public class InitAttributeable extends MobBehaviour {
    public InitAttributeable(CustomMob mob) {
        super(mob);
    }

    public void setConfiguredAttributes() {
        EntityLiving entity = mob.getEntity();
        MobTemplate builder = mob.getStaticTemplate();

        if (builder == null)
            return;

        if (builder.getMaxHealth() != null)
            entity.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(builder.getMaxHealth());
        if (builder.getMovementSpeed() != null)
            entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(builder.getMovementSpeed());
        /*if (builder.getAttackDamage() != null && !(mob instanceof FlyingMob)) //flying mobs can't have an attack damage attribute, we use the builder's value instead
            entity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(builder.getAttackDamage());*/ //FIXME
        if (builder.getFollowRange() != null)
            entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(builder.getFollowRange());
    }
}
