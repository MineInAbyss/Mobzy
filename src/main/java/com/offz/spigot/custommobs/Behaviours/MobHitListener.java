package com.offz.spigot.custommobs.Behaviours;

import com.offz.spigot.custommobs.CustomMobs;
import com.offz.spigot.custommobs.MobContext;
import com.offz.spigot.custommobs.MobType.MobType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MobHitListener implements Listener {
    private MobContext context;
    CustomMobs plugin;

    public MobHitListener(MobContext context) {
        this.context = context;
        plugin = (CustomMobs) context.getPlugin();
    }

    @EventHandler
    public void onHit(EntityDamageEvent e) {
        MobType type = MobType.getRegisteredMobType(e.getEntity());

        if (type != null) {
            if (type.getBehaviour() instanceof HitBehaviour) {
                ((HitBehaviour) type.getBehaviour()).onHit(e);

                Zombie entity = (Zombie) e.getEntity();
                EntityEquipment ee = entity.getEquipment();
                ItemStack is = ee.getHelmet();
                is.setDurability((short) (MobType.getRegisteredMobType(e.getEntity()).getModelID() + 2));
                ee.setHelmet(is);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        is.setDurability((short) (MobType.getRegisteredMobType(e.getEntity()).getModelID() + 1));
                        ee.setHelmet(is);
                    }
                }.runTaskLater(plugin, 5);
            } else {
                e.setCancelled(true);
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