package com.offz.spigot.custommobs.Mobs.Behaviours;

import com.offz.spigot.custommobs.Behaviours.WalkingMobBehaviour;
import com.offz.spigot.custommobs.Behaviours.DeathBehaviour;
import com.offz.spigot.custommobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class NeritantanBehaviour implements WalkingMobBehaviour, HitBehaviour, DeathBehaviour, MobBehaviour{

    MobType type;

    @Override
    public void onHit(EntityDamageEvent e) {
    }

    @Override
    public void onDeath(EntityDeathEvent e) {
        for (Entity passanger : e.getEntity().getPassengers())
            passanger.remove();
    }

    @Override
    public void animate(WalkingMobBehaviour.MobInfo mob) {
        Zombie e = (Zombie) mob.entity;
        EntityEquipment ee = e.getEquipment();

        ItemStack is = ee.getHelmet();
        if(is.getDurability() == mob.hitDamageValue)
            return;
        if (e.getVelocity().getX() == 0 && e.getVelocity().getZ() == 0)
            is.setDurability(mob.stillDamageValue);
        else
            is.setDurability(mob.movingDamageValue);
        ee.setHelmet(is);
    }

    @Override
    public void setMobType(MobType type) {
        this.type = type;
    }
}