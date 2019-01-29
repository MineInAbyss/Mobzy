package com.offz.spigot.custommobs.Mobs.NPCs;

import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.EntityVillager;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Ozen extends EntityVillager {
    public Ozen(World world) {
        super(world);
        Villager ozen = (Villager) this.getBukkitEntity();

        ozen.setAdult();

        this.addScoreboardTag("customMob");
        ozen.setCustomName("Ozen");
        this.setCustomNameVisible(true);
        this.setSilent(true);
        this.setInvulnerable(true);
        ozen.setRemoveWhenFarAway(false);

        ozen.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));

        MobType type = MobType.getRegisteredMobType(ozen);
        AnimationBehaviour.registerMob(ozen, type, type.getModelID());

        this.getWorld().addEntity(this);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
    }
}
