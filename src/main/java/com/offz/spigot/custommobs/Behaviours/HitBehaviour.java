package com.offz.spigot.custommobs.Behaviours;


import com.offz.spigot.custommobs.Mobs.MobBehaviour;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface HitBehaviour extends MobBehaviour {
    public void onHit(EntityDamageByEntityEvent event);
}
