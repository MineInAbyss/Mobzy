package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.EntityPig;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.entity.Pig;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Fuwagi extends EntityPig {
    public Fuwagi(World world) {
        super(world);
        Pig fuwagi = (Pig) this.getBukkitEntity();

        fuwagi.setBaby();

        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<Neritantan>(this, Neritantan.class, true));

        this.addScoreboardTag("customMob");
        fuwagi.setCustomName("Fuwagi");
        this.setCustomNameVisible(false);
        this.setSilent(true);
        fuwagi.setRemoveWhenFarAway(true);

        fuwagi.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));

        MobType type = MobType.getRegisteredMobType(fuwagi);
        AnimationBehaviour.registerMob(fuwagi, type, type.getModelID());

        this.getWorld().addEntity(this);
    }

    @Override
    public void k() {
        super.k();
        this.getBukkitEntity().setFireTicks(0);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.2);
    }
}
