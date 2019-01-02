package com.offz.spigot.custommobs.Behaviours;

import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import org.bukkit.event.entity.EntityDeathEvent;

public interface DeathBehaviour extends MobBehaviour {
    void onDeath(EntityDeathEvent e);
}
