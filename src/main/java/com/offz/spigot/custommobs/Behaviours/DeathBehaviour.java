package com.offz.spigot.custommobs.Behaviours;

import com.offz.spigot.custommobs.Mobs.MobBehaviour;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public interface DeathBehaviour extends MobBehaviour {
    public void onDeath(EntityDeathEvent e);
}
