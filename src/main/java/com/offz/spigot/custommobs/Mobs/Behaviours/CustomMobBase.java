package com.offz.spigot.custommobs.Mobs.Behaviours;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.CustomMobs;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        MobBuilder builder = mob.getBuilder();
        setConfiguredAttributes();

        entity.setSize(0.5F, 0.5F);
        entity.addScoreboardTag("customMob2");
        entity.addScoreboardTag(builder.getName());

        LivingEntity asLiving = ((LivingEntity) entity.getBukkitEntity());

        //create an item based on model ID in head slot if entity will be using itself for the model
        ItemStack is = new ItemStack(builder.getModelMaterial());
        is.setDurability((short) builder.getModelID());
        ItemMeta meta = is.getItemMeta();
        meta.setUnbreakable(true);
        is.setItemMeta(meta);
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
        FileConfiguration mobsCfg = CustomMobs.getPlugin(CustomMobs.class).getConfigManager().getMobsCfg();
        String name = mob.getBuilder().getName();
        if (mobsCfg.contains(name + ".max_health"))
            entity.getAttributeInstance(GenericAttributes.maxHealth).setValue(mobsCfg.getDouble(name + ".max_health"));
        if (mobsCfg.contains(name + ".movement_speed"))
            entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(mobsCfg.getDouble(name + ".movement_speed"));
        if (mobsCfg.contains(name + ".attack_damage"))
            entity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(mobsCfg.getDouble(name + ".attack_damage"));
        if (mobsCfg.contains(name + ".follow_range"))
            entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(mobsCfg.getDouble(name + ".follow_range"));
    }
}
