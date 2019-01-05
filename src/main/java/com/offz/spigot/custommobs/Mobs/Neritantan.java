package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.Behaviours.WalkingBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.entity.Pig;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Neritantan extends EntityPig {
    public Neritantan(World world) {
        super(world);
        Pig neritantan = (Pig) this.getBukkitEntity();

        neritantan.setMaxHealth(10);
        this.setHealth(10);
        neritantan.setBaby();

        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, RecipeItemStack.a(new IMaterial[]{Items.CARROT,Items.APPLE}), false));

//        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTargetInsentient(this, EntityIronGolem.class));

        this.addScoreboardTag("customMob");
        neritantan.setCustomName("Neritantan");
        this.setCustomNameVisible(false);
        this.setSilent(true);
        neritantan.setRemoveWhenFarAway(false);

        neritantan.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));

        MobType type = MobType.getRegisteredMobType(neritantan);
        WalkingBehaviour.registerMob(neritantan, type, type.getModelID());

        this.getWorld().addEntity(this);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3);
    }
}
