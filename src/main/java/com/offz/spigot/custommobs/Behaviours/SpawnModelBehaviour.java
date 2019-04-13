package com.offz.spigot.custommobs.Behaviours;

import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface SpawnModelBehaviour extends MobBehaviour {
    static void spawnModel(Entity e, MobType type) {
        AreaEffectCloud aec = (AreaEffectCloud) e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.AREA_EFFECT_CLOUD);
        aec.setDuration(2147483600);
        aec.clearCustomEffects();
        aec.setRadius(0f);
        //TODO The AEC spawns some particles for a bit, TOWN_AURA looks like the least noticeable particle, but there might be a way of getting completely invisible ones
        aec.setParticle(Particle.TOWN_AURA);
        aec.addScoreboardTag("customMob");
        aec.addScoreboardTag("additionalPart");

        ArmorStand as = (ArmorStand) e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.ARMOR_STAND);
        as.setGravity(false);
        as.setVisible(false);
        as.setArms(false);
        as.setSilent(true);
        as.setBasePlate(false);
        as.setMarker(true);
        as.addScoreboardTag("customMob");
        as.addScoreboardTag("additionalPart");

        as.setCustomNameVisible(false);
        ItemStack is = new ItemStack(type.getMaterial());
        is.setDurability(type.getModelID());

        ItemMeta meta = is.getItemMeta();
        meta.setUnbreakable(true);
        is.setItemMeta(meta);

        as.getEquipment().setHelmet(is);
        aec.addPassenger(as);
        e.addPassenger(aec);
    }
}
