package com.offz.spigot.custommobs.Mobs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Neritantan {
    public Entity Neritantan (Entity e){
        Location location = e.getLocation();
        e.remove();
        e = location.getWorld().spawnEntity(location, EntityType.SLIME);
        Bukkit.getServer().broadcastMessage("EntityZombie Spawned");

        e.setInvulnerable(true);

        ((Slime) e).setSize(1);
        ((Slime) e).setAI(true);

        ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        e.addPassenger(as);
        as.setGravity(false);
        as.setArms(true);
        as.setVisible(false);
        as.setCollidable(false);
        as.setInvulnerable(true);

        ItemStack is = as.getHelmet();
        is.setType(Material.DIAMOND_HOE);
        is.setDurability((short) 7);

        ItemMeta meta = is.getItemMeta();
        meta.setUnbreakable(true);
        is.setItemMeta(meta);

        as.setHelmet(is);
        return e;
    }
}
