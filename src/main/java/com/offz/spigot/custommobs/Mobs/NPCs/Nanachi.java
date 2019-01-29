package com.offz.spigot.custommobs.Mobs.NPCs;

import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.EntityVillager;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Nanachi extends EntityVillager {
    public Nanachi(World world) {
        super(world);
        Villager nanachi = (Villager) this.getBukkitEntity();

        nanachi.setAdult();

        this.addScoreboardTag("customMob");
        nanachi.setCustomName("Nanachi");
        this.setCustomNameVisible(true);
        this.setSilent(true);
        this.setInvulnerable(true);
        nanachi.setRemoveWhenFarAway(false);

        nanachi.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));

        MobType type = MobType.getRegisteredMobType(nanachi);
        AnimationBehaviour.registerMob(nanachi, type, type.getModelID());

        this.getWorld().addEntity(this);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
    }
}
