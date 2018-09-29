package com.offz.spigot.custommobs.Spawning;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import javax.swing.text.html.parser.Entity;

public class SpawnListener implements Listener {

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e){
        switch(e.getEntity().getType()){
            case ZOMBIE:
                SpawnMob.Neritantan(e.getEntity());
            default:
                break;
        }
    }
    @EventHandler()
    public void onPlayerInteractEntity(PlayerInteractAtEntityEvent e) {
    }
    Entity spawn(Entity e){
        return e;
    }
}
