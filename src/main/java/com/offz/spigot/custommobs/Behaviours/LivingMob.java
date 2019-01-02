package com.offz.spigot.custommobs.Behaviours;

import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class LivingMob implements WalkingMobBehaviour, HitBehaviour, DeathBehaviour, MobBehaviour {

    @Override
    public void onHit(EntityDamageEvent e) {

    }

    @Override
    public void onDeath(EntityDeathEvent e) {
        WalkingMobBehaviour.registeredMobs.remove(e);
        for (Entity passanger : e.getEntity().getPassengers())
            passanger.remove();
    }

    @Override
    public void animate(MobInfo mob) {
        Entity e = (Entity) mob.entity;
        ArmorStand as = (ArmorStand) e.getPassengers().get(0);
        EntityEquipment ee = as.getEquipment();
        ItemStack is = ee.getHelmet();
        if(is.getDurability() == mob.hitDamageValue)
            return;
        if (e.getVelocity().getX() == 0 && e.getVelocity().getZ() == 0)
            is.setDurability(mob.stillDamageValue);
        else
            is.setDurability(mob.movingDamageValue);
        ee.setHelmet(is);
    }
}
