package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.Behaviours.WalkingBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.EntityZombie;
import net.minecraft.server.v1_13_R2.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Fuwagi extends EntityZombie {
    public Fuwagi(World world) {
        super(world);
        Zombie fuwagi = (Zombie) this.getBukkitEntity();

        this.setBaby(true);

        fuwagi.setMaxHealth(10);
        this.setHealth(10);

        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<Neritantan>(this, Neritantan.class, true));

        this.addScoreboardTag("customMob");
        fuwagi.setCustomName("Fuwagi");
        this.setCustomNameVisible(false);
        this.setSilent(true);

        fuwagi.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));

        MobType type = MobType.getRegisteredMobType(fuwagi);
        WalkingBehaviour.registerMob(fuwagi, type, type.getModelID());

        this.getWorld().addEntity(this);
    }
}
