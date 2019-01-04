package com.offz.spigot.custommobs.Behaviours.Listener;

import com.offz.spigot.custommobs.Behaviours.*;
import com.offz.spigot.custommobs.CustomMobs;
import com.offz.spigot.custommobs.MobContext;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

public class MobListener implements Listener {
    private MobContext context;
    CustomMobs plugin;

    public MobListener(MobContext context) {
        this.context = context;
        plugin = (CustomMobs) context.getPlugin();
    }

    @EventHandler
    public void onHit(EntityDamageEvent e) {
        MobType type = MobType.getRegisteredMobType(e.getEntity());

        if (type != null) {
            if (type.getBehaviour() instanceof HitBehaviour && type.getBehaviour() instanceof SpawnModelBehaviour) {
                ((HitBehaviour) type.getBehaviour()).onHit(e);

                Monster entity = (Monster) e.getEntity();
                EntityEquipment ee = ((ArmorStand) entity.getPassengers().get(0).getPassengers().get(0)).getEquipment();
                ItemStack is = ee.getHelmet();
                is.setDurability((short) (MobType.getRegisteredMobType(e.getEntity()).getModelID() + 2));
                ee.setHelmet(is);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!e.getEntity().isDead()) {
                            is.setDurability((short) (MobType.getRegisteredMobType(e.getEntity()).getModelID() + 1));
                            ee.setHelmet(is);
                        }
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
        if (type != null) {
            Entity entity = e.getEntity();
            if (type.getBehaviour() instanceof WalkingBehaviour)
                ((WalkingBehaviour) type.getBehaviour()).onDeath(entity.getUniqueId());
            if (type.getBehaviour() instanceof SpawnModelBehaviour) {
                entity.getPassengers().get(0).getPassengers().get(0).remove();
                entity.getPassengers().get(0).remove();
            }
            if (type.getBehaviour() instanceof DeathBehaviour) {
                e.setCancelled(true);
                ((DeathBehaviour) type.getBehaviour()).onDeath(e);
                Location loc = entity.getLocation();
                entity.getWorld().spawnParticle(Particle.CLOUD, loc.add(0, 0.75, 0), 10, 0.1, 0.25, 0.1, 0.025);
                e.getEntity().remove();
            }
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        MobType type = MobType.getRegisteredMobType(e.getEntity());
        if (type != null) {
            if (type.getBehaviour() instanceof SpawnBehaviour)
                ((SpawnBehaviour) type.getBehaviour()).onSpawn(e);
            if (type.getBehaviour() instanceof SpawnModelBehaviour) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ArmorStand aec = (ArmorStand) e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.ARMOR_STAND);
                        aec.setGravity(false);
                        aec.setVisible(false);
                        aec.setArms(true);
                        aec.setSilent(true);
                        aec.setMarker(true);
                        aec.addScoreboardTag("customMob");

                        ArmorStand as = (ArmorStand) e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.ARMOR_STAND);
                        as.setGravity(false);
                        as.setVisible(false);
                        as.setArms(true);
                        as.setSilent(true);
                        as.setMarker(true);
                        as.addScoreboardTag("customMob");

                        as.setCustomNameVisible(false);
                        ItemStack is = new ItemStack(org.bukkit.Material.DIAMOND_SWORD);
                        is.setDurability(type.getModelID());

                        ItemMeta meta = is.getItemMeta();
                        meta.setUnbreakable(true);
                        is.setItemMeta(meta);

                        as.getEquipment().setHelmet(is);
                        aec.addPassenger(as);
                        e.getEntity().addPassenger(aec);
                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }

    @EventHandler
    public void onVeichleExit(EntityDismountEvent e) {
        Entity entity = e.getDismounted();
        if (entity.getScoreboardTags().contains("customMob")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        for (Entity entity : e.getChunk().getEntities()) {
            try {
                Bukkit.broadcastMessage("Loaded");
                if (entity != null) {
                    MobType type = MobType.getRegisteredMobType(entity);
                    if (type != null && type.getBehaviour() instanceof WalkingBehaviour && !WalkingBehaviour.registeredMobs.containsKey(entity.getUniqueId())) {
                        WalkingBehaviour.registerMob(entity, type, type.getModelID());
                    }
                }
            } catch (NullPointerException npe) {

            }
        }
    }
}