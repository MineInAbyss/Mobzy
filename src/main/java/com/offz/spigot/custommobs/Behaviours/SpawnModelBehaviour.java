package com.offz.spigot.custommobs.Behaviours;

import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface SpawnModelBehaviour extends MobBehaviour {
    static void spawnModel(Entity e, MobType type) {
        ArmorStand aec = (ArmorStand) e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.ARMOR_STAND);
        aec.setGravity(false);
        aec.setVisible(false);
        aec.setArms(true);
        aec.setSilent(true);
        aec.setMarker(true);
        aec.addScoreboardTag("customMob");

        ArmorStand as = (ArmorStand) e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.ARMOR_STAND);
        as.setGravity(false);
        as.setVisible(false);
        as.setArms(true);
        as.setSilent(true);
        as.setMarker(true);
        as.addScoreboardTag("customMob");

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
