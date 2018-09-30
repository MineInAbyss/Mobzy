package com.offz.spigot.custommobs.Spawning;

import com.offz.spigot.custommobs.Behaviours.AI.WalkingMobBehaviour;
import com.offz.spigot.custommobs.MobType.MobType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpawnMob {
    public static Entity Neritantan(Entity e) {
        Location location = e.getLocation();
//        e.remove();
//        e = location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
//        Bukkit.getServer().broadcastMessage("EntityZombie Spawned");

        e.addScoreboardTag("customMob");
        e.setCustomName("Neritantan");
        e.setCustomNameVisible(false);
        e.setSilent(true);

        ((Zombie) e).setBaby(true);
        ((Zombie) e).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));

//        ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND); //For mobs requiring an armorstand passanger
//        e.addPassenger(as);
//        as.setGravity(false);
//        as.setVisible(false);
//        as.setArms(true);
//        as.setMarker(true);

        EntityEquipment equips = ((Zombie) e).getEquipment();

        ItemStack is = equips.getHelmet();
        is.setType(Material.DIAMOND_SWORD);
        is.setDurability((short) 2);

        ItemMeta meta = is.getItemMeta();
        meta.setUnbreakable(true);
        is.setItemMeta(meta);

        equips.setHelmet(is);

        MobType type = MobType.getRegisteredMobType(e);

        WalkingMobBehaviour.registerMob(e, type, type.getModelID());

        return e;
    }
}