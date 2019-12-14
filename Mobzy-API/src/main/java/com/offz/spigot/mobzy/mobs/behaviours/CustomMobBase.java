package com.offz.spigot.mobzy.mobs.behaviours;

import com.offz.spigot.mobzy.mobs.MobTemplate;
import com.offz.spigot.mobzy.mobs.CustomMob;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.minecraft.server.v1_13_R2.EntityLiving;
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
        MobTemplate builder = mob.getBuilder(); //get the mob's class name

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
}
