package com.offz.spigot.custommobs.Spawning;

import com.derongan.minecraft.deeperworld.world.WorldManager;
import com.derongan.minecraft.mineinabyss.AbyssContext;
import com.derongan.minecraft.mineinabyss.world.AbyssWorldManager;
import com.offz.spigot.custommobs.Loading.SpawnRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpawnTask extends BukkitRunnable {
    private AbyssContext context;
    private WorldManager worldManager;

    public SpawnTask(AbyssContext context) {
        this.context = context;
        this.worldManager = context.getRealWorldManager();
    }

    //TODO decide if we need to remove, the same code is present in the MobSpawn class
    /*public static boolean spawnMultiple(String name, Location p, int amount, int radius) {
        return spawnMultiple(name, p, amount, radius, null);
    }

    public static boolean spawnMultiple(String name, Location p, int amount, int radius, List<Material> spawnWhitelist) {
        int spawned = 0;

        for (int i = 0; i < amount; i++) {
            Location l = getSpawnLocation(p, 0, radius);

            if (l != null && (spawnWhitelist == null || spawnWhitelist.contains(l.add(0, -1, 0).getBlock().getType()))) {
                SpawnListener.spawnEntity(name, l);
                spawned += 1;
            }
        }
        Bukkit.broadcastMessage(spawned + " spawned");
        return spawned > 0;
    }*/

    public static Location getSpawnLocation(Location p, double minRad, double maxRad) {
        for (int i = 0; i < 30; i++) {
            double y = (Math.random() - 0.5) * maxRad;
            if (Math.abs(y) > minRad) //if y is minRad blocks away from player, mobs can spawn directly under or above
                minRad = 0;

            double x = Math.signum(Math.random() - 0.5) * ((Math.random() * (maxRad - minRad)) + minRad);
            double z = Math.signum(Math.random() - 0.5) * ((Math.random() * (maxRad - minRad)) + minRad);
            if (!p.isChunkLoaded())
                return null;
            Location l = p.toBlockLocation();
            l = l.add(new Vector(x, y, z));

            //TODO I'd like this to go straight down for entity couple blocks, then diagonally in 4 sides
            if (!l.getBlock().getType().isSolid()) {
                l = checkDown(l, 25);
                if (l != null)
                    return l;
            } else {
                l = checkUp(l, 25);
                if (l != null)
                    return l;
            }
//            if(l == null)
//                Bukkit.broadcastMessage("getSpawnLocation Nulled");
        }
        return null;
    }

    public static Location checkDown(Location originalL, int maxI) {
        Location l = originalL.toBlockLocation();
        for (int i = 0; i < maxI; i++) {
            l = l.add(0, -1, 0);

            if (l.getY() < 10)
                return null;
            if (l.getY() >= 256)
                l.setY(255);

            if (l.getBlock().getType().isSolid()) {
                return l.add(0, 1, 0);
            }
        }
        return null;
    }

    public static Location checkUp(Location originalL, int maxI) {
        Location l = originalL.toBlockLocation();
        for (int i = 0; i < maxI; i++) {
            l = l.add(0, 1, 0);

            if (!l.getBlock().getType().isSolid()) {
                return l;
            }

            if (l.getY() >= 256)
                return null;
            if (l.getY() < 10)
                l.setY(10);
        }
        return null;
    }

    @Override
    public void run() {
        AbyssWorldManager manager = context.getWorldManager();
        List<UUID> closePlayers = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uuid = p.getUniqueId();
            if (closePlayers.contains(uuid)) { //if this player has been registered as close to another, do not do spawns
                closePlayers.remove(uuid);
                continue;
            }

            int mobCount = 0;
            for (Entity e : p.getNearbyEntities(128, 256, 128)) {
                if (e.getScoreboardTags().contains("MOB"))
                    mobCount++;
                if (e.getType().equals(EntityType.PLAYER))
                    closePlayers.add(e.getUniqueId());
            }

            if (manager.isAbyssWorld(p.getWorld())) {
                String layerName = manager.getLayerForSection(worldManager.getSectionFor(p.getLocation())).getName();
                if (mobCount < 20) {
                    Location l = getSpawnLocation(p.getLocation(), 20, 30);
                    if (l == null) {
//                        Bukkit.broadcastMessage("getSpawnLocation nulled");
                        return;
                    }

//                    Bukkit.broadcastMessage(mobCount + " mobs");

                    //get the list of mob spawns for entity layer, then remove all impossible spawns, and make entity weighted decision on the spawn
                    RandomCollection<MobSpawn> validSpawns = new RandomCollection<>();

                    for (MobSpawn spawn : SpawnRegistry.getLayerSpawns().get(layerName)) {
                        double weight = spawn.getPriority(l);
                        validSpawns.add(weight, spawn);
                    }

                    if (validSpawns.isEmpty()) {
//                        Bukkit.broadcastMessage("Spawn Failed");
                        return;
                    }

                    //weighted random decision of valid spawn
                    MobSpawn spawn = validSpawns.next();
                    spawn.spawn(l);
//                    Bukkit.broadcastMessage("Spawned " + spawn.getMobID());
                }
            }
        }
    }
}