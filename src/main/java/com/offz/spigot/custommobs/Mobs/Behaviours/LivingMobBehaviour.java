package com.offz.spigot.custommobs.Mobs.Behaviours;

import com.offz.spigot.custommobs.Behaviours.*;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class LivingMobBehaviour implements HitBehaviour, DeathBehaviour, MobBehaviour, SpawnModelBehaviour, AnimationBehaviour, WalkingBehaviour, HeadRotateBehaviour {

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

    @Override
    public void animate(MobInfo mob) {

    }

    @Override
    public void walk(MobInfo mob) {

    }

    @Override
    public void rotateHead(MobInfo mob) {

    }
}