package com.offz.spigot.custommobs.Mobs.NPCs;

import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.EntityVillager;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Mitty extends EntityVillager {
    public Mitty(World world) {
        super(world);
        Villager mitty = (Villager) this.getBukkitEntity();

        mitty.setBaby();

        this.addScoreboardTag("customMob");
        mitty.setCustomName("Mitty");
        this.setCustomNameVisible(true);
        this.setSilent(true);
        this.setInvulnerable(true);
        mitty.setRemoveWhenFarAway(false);

        mitty.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));

        MobType type = MobType.getRegisteredMobType(mitty);
        AnimationBehaviour.registerMob(mitty, type, type.getModelID());

        this.getWorld().addEntity(this);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
    }
}
