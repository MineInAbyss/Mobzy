package com.offz.spigot.mobzy.Mobs.Behaviours;

import com.offz.spigot.mobzy.Builders.MobBuilder;
import com.offz.spigot.mobzy.Mobs.CustomMob;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

//TODO this is kinda weird, there's probably a better structure to go for
public class CustomMobBase extends MobBehaviour {
    public CustomMobBase(CustomMob mob) {
        super(mob);
    }

    /**
     * Applies some default attributes that every custom mob should have, such as a model, invisibility, and an
     * identifier scoreboard tag
     */
    public void apply() {
        EntityLiving entity = mob.getEntity();
        MobBuilder builder = mob.getBuilder(); //get the mob's class name
        setConfiguredAttributes();

        entity.setSize(0.5F, 0.5F);
        entity.addScoreboardTag("customMob2");
        entity.addScoreboardTag(builder.getName());

        LivingEntity asLiving = ((LivingEntity) entity.getBukkitEntity());

        //create an item based on model ID in head slot if entity will be using itself for the model
        ItemStack is = builder.getModelItemStack();
        asLiving.getEquipment().setHelmet(is);

        if (builder.getDisguiseAs() != null) {
            //disguise the entity
            Disguise disguise = new MobDisguise(builder.getDisguiseAs(), builder.isAdult());
            DisguiseAPI.disguiseEntity(entity.getBukkitEntity(), disguise);
            disguise.getWatcher().setInvisible(true);
        }
    }

    private void setConfiguredAttributes() {
        EntityLiving entity = mob.getEntity();
        MobBuilder builder = mob.getBuilder();

        if (builder.getMaxHealth() != null)
            entity.getAttributeInstance(GenericAttributes.maxHealth).setValue(builder.getMaxHealth());
        if (builder.getMovementSpeed() != null)
            entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(builder.getMovementSpeed());
        if (builder.getAttackDamage() != null)
            entity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(builder.getAttackDamage());
        if (builder.getFollowRange() != null)
            entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(builder.getFollowRange());
    }
}
