package com.offz.spigot.custommobs.Behaviours;


import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import org.bukkit.event.entity.EntityDamageEvent;

public interface HitBehaviour extends MobBehaviour {
    void onHit(EntityDamageEvent event);
}
