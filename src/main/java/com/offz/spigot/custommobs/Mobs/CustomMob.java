package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.SoundEffect;
import net.minecraft.server.v1_13_R2.SoundEffects;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    default void createCustomMob(World world, MobBuilder builder, Entity entity) {
        entity.setSize(0.5F, 0.5F);
        entity.addScoreboardTag("customMob2");
        entity.setCustomNameVisible(true);
        entity.addScoreboardTag(builder.getName());

        LivingEntity asLiving = ((LivingEntity) entity.getBukkitEntity());
        asLiving.setCustomName(builder.getName());

        //create an item based on model ID in head slot if entity will be using itself for the model
        ItemStack is = new ItemStack(builder.getModelMaterial());
        is.setDurability((short) builder.getModelID());
        ItemMeta meta = is.getItemMeta();
        meta.setUnbreakable(true);
        is.setItemMeta(meta);
        asLiving.getEquipment().setHelmet(is);

        //TODO this really doesn't like armorstands
        //disguise the entity
        Disguise disguise = new MobDisguise(builder.getDisguiseAs(), builder.isAdult());
        DisguiseAPI.disguiseEntity(entity.getBukkitEntity(), disguise);
        disguise.getWatcher().setInvisible(true);
//        asAnimal.setRemoveWhenFarAway(true);
    }


    //TODO This might be useful for multi entity mobs later
    /*default Model spawnModel(World world, MobBuilder builder, EntityLiving entity) {
        if (!(entity instanceof SecondaryModelBehaviour))
            return null;

        Model model = new Model(world);
        model.setPosition(entity.locX, entity.locY, entity.locZ);

        //create an item based on model ID in head slot
        org.bukkit.inventory.ItemStack is = new ItemStack(builder.getModelMaterial());
        is.setDurability((short) builder.getModelID());
        ItemMeta meta = is.getItemMeta();
        meta.setUnbreakable(true);
        is.setItemMeta(meta);
        ((LivingEntity) model.getBukkitEntity()).getEquipment().setHelmet(is);
        model.setInvisible(true);
        model.setMarker(true);
        model.setSilent(true);
        model.addScoreboardTag("additionalPart");

//        EntityArmorStand empty = new EntityArmorStand(world);
//        empty.setPosition(entity.locX, entity.locY, entity.locZ);
//        empty.addScoreboardTag("additionalPart");

        entity.passengers.add(model);
//        empty.passengers.add(model);

//        world.addEntity(empty);
        world.addEntity(model);
        return model;
    }*/
}