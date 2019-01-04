package com.offz.spigot.custommobs.Behaviours;

import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import org.bukkit.event.entity.EntitySpawnEvent;

public interface SpawnBehaviour extends MobBehaviour {
    void onSpawn(EntitySpawnEvent e);
}
