package com.offz.spigot.custommobs.Behaviours;

import com.offz.spigot.custommobs.MobContext;
import com.offz.spigot.custommobs.MobType.MobType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobHitListener implements Listener {
    private MobContext context;

    public MobHitListener(MobContext context) {
        this.context = context;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            MobType type = MobType.getRegisteredMobType(e.getEntity());

            if (type != null) {
                if (type.getBehaviour() instanceof HitBehaviour) {
                    ((HitBehaviour) type.getBehaviour()).onHit(e);
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        MobType type = MobType.getRegisteredMobType(e.getEntity());
        if (type != null && type.getBehaviour() instanceof DeathBehaviour) {
            ((DeathBehaviour) type.getBehaviour()).onDeath(e);
        }
    }
}