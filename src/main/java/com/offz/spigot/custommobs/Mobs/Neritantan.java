package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.Behaviours.WalkingMobBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Neritantan extends EntityZombie {
    public Neritantan(World world) {
        super(world);
        Zombie neritantan = (Zombie) this.getBukkitEntity();

        this.setBaby(true);

        neritantan.setMaxHealth(50);
        this.setHealth(50);

        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<EntityPig>(this, EntityPig.class, true));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<EntityChicken>(this, EntityChicken.class, true));

        this.addScoreboardTag("customMob");
        neritantan.setCustomName("Neritantan");
        this.setCustomNameVisible(false);
        this.setSilent(true);

        neritantan.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));

        EntityEquipment equips = neritantan.getEquipment();
        ItemStack is = equips.getHelmet();
        is.setType(org.bukkit.Material.DIAMOND_SWORD);
        is.setDurability((short) 2);

        ItemMeta meta = is.getItemMeta();
        meta.setUnbreakable(true);
        is.setItemMeta(meta);

        equips.setHelmet(is);

        MobType type = MobType.getRegisteredMobType(neritantan);
        WalkingMobBehaviour.registerMob(neritantan, type, type.getModelID());

        this.getWorld().addEntity(this);
    }
    // TODO add custom stuffs to make it custom
}
