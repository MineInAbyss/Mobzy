package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.EntityZombie;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Rohana extends EntityZombie {
    public Rohana(World world) {
        super(world);
        Zombie rohana = (Zombie) this.getBukkitEntity();

        rohana.setBaby(true);

        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<Neritantan>(this, Neritantan.class, true));

        this.addScoreboardTag("customMob");
        rohana.setCustomName("Rohana");
        this.setCustomNameVisible(false);
        this.setSilent(true);
        rohana.setRemoveWhenFarAway(true);

        rohana.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));

        MobType type = MobType.getRegisteredMobType(rohana);
        AnimationBehaviour.registerMob(rohana, type, type.getModelID());


        this.getWorld().addEntity(this);

    }

    @Override
    public void k() {
        super.k();
        this.getBukkitEntity().setFireTicks(0);

        Zombie rohana = ((Zombie) this.getBukkitEntity());
        rohana.getEquipment().clear();

        MobType type = MobType.getRegisteredMobType(rohana);
        ItemStack is = new ItemStack(org.bukkit.Material.DIAMOND_SWORD);
        is.setDurability(type.getModelID());

        ItemMeta meta = is.getItemMeta();
        meta.setUnbreakable(true);
        is.setItemMeta(meta);

        rohana.getEquipment().setHelmet(is);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(5.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(0.2D);
    }
}
