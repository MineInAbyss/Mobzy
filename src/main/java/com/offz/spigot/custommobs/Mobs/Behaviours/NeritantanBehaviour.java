package com.offz.spigot.custommobs.Mobs.Behaviours;

import com.offz.spigot.custommobs.Behaviours.DeathBehaviour;
import com.offz.spigot.custommobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Behaviours.SpawnModelBehaviour;
import com.offz.spigot.custommobs.Behaviours.WalkingBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class NeritantanBehaviour implements WalkingBehaviour, HitBehaviour, DeathBehaviour, MobBehaviour, SpawnModelBehaviour {

    MobType type;

    @Override
    public void onHit(EntityDamageEvent e) {
    }

    @Override
    public void onDeath(EntityDeathEvent e) {
    }

    @Override
    public void setMobType(MobType type) {
        this.type = type;
    }
}