package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import com.offz.spigot.custommobs.Behaviours.SpawnModelBehaviour;
import com.offz.spigot.custommobs.Builders.IAttributeBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.GroundMobType;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public interface CustomMob {

    default void createCustomMob(EntityLiving entity, String[] additionalTags) {
        for (String tag : additionalTags)
            entity.addScoreboardTag(tag);
        createCustomMob(entity);
    }

    default void createCustomMob(EntityLiving entity) {
        entity.addScoreboardTag("customMob");
        entity.setCustomNameVisible(false);
        entity.setSilent(true);

        //TODO This might not be able to cast
        LivingEntity asLiving = ((LivingEntity) entity.getBukkitEntity());
        asLiving.setRemoveWhenFarAway(true);
        asLiving.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));

        //run any additional code that we've defined while creating the mob
        addAttributes();

        entity.getWorld().addEntity(entity);
    }

    default CraftEntity registerBehaviours(IChatBaseComponent iCBC, EntityLiving entity) {
        //TODO I don't know if separating mob types honestly changes anything, maybe make every type extend some main enum?
        GroundMobType type = (GroundMobType) MobType.getRegisteredMobType(iCBC.getString());
        return registerBehaviours(entity, type, type.getBehaviour());
    }

    default CraftEntity registerBehaviours(EntityLiving entity, GroundMobType type, MobBehaviour behaviour) {
        CraftEntity mob = entity.getBukkitEntity();

        mob.addScoreboardTag(type.getEntityTypeID());
        mob.setCustomName(type.getName());

        if (behaviour instanceof SpawnModelBehaviour)
            SpawnModelBehaviour.spawnModel(mob, type);
        if (behaviour instanceof AnimationBehaviour)
            AnimationBehaviour.registerMob(mob, type, type.getModelID());

        return mob;
    }

    default void setInitAttributes(EntityLiving entity, IAttributeBuilder iab) {
        if (iab.getAttackDamage() != null)
            entity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(iab.getAttackDamage());
        if (iab.getFollowRange() != null)
            entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(iab.getFollowRange());
        if (iab.getMaxHealth() != null)
            entity.getAttributeInstance(GenericAttributes.maxHealth).setValue(iab.getMaxHealth());
        if (iab.getMovementSpeed() != null)
            entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(iab.getMovementSpeed());

    }



    default void addAttributes() {
    }
}
