package com.offz.spigot.custommobs.Spawning;

import com.derongan.minecraft.deeperworld.world.WorldManager;
import com.derongan.minecraft.mineinabyss.AbyssContext;
import com.derongan.minecraft.mineinabyss.world.AbyssWorldManager;
import com.offz.spigot.custommobs.CustomMobs;
import com.offz.spigot.custommobs.CustomMobsAPI;
import com.offz.spigot.custommobs.Loading.SpawnRegistry;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
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
    private final int MOB_CAP = 20;
    private final int MAX_TRIES = 100;
    private CustomMobs plugin;

    public SpawnTask(CustomMobs plugin, AbyssContext context) {
        this.context = context;
        this.worldManager = context.getRealWorldManager();
        this.plugin = plugin;
    }

    public static SpawnArea getSpawnArea(Location spawnLoc, double minRad, double maxRad) {
        for (int i = 0; i < 30; i++) { //gets 30 tries
            //TODO the new system doesn't really allow us to spawn anything above and below the player easily; figure something out!
            /*double y = (Math.random() - 0.5) * maxRad / 10;
            if (Math.abs(y) > minRad) //if y is minRad blocks away from player, mobs can spawn directly under or above
                minRad = 0;*/

            //pick offset based on min and max radius
            double x, y, z, d;
            double mag = (maxRad - minRad) + minRad;
            do {
                x = (Math.random() * 2 - 1) * mag;
//                y = (Math.random() * 2 - 1) * mag;
                y = 0;
                z = (Math.random() * 2 - 1) * mag;
            } while((d = x*x + y*y + z*z) > maxRad*maxRad); //loop if it's outside of the sphere


            if (!spawnLoc.isChunkLoaded())
                continue;
            Location checkLoc = spawnLoc.toBlockLocation();
            checkLoc = checkLoc.add(new Vector(x, y, z));

            //get a list of spawn locations for that y value
            List<SpawnArea> spawnAreas = findBlockPairs(checkLoc, spawnLoc.getBlockY() - (int) maxRad, spawnLoc.getBlockY() + (int) maxRad);
            if (spawnAreas.isEmpty()) //if there were no blocks there, try again
                continue;

//            CustomMobsAPI.debug(spawnAreas.toString());
            //weighted choice based on gap size
            RandomCollection<SpawnArea> weightedChoice = new RandomCollection<>();
            for (SpawnArea spawnArea : spawnAreas) { //add each
                int weight = spawnArea.getGap();
                if (weight > 100)
                    weight = 100; //make underground spawns a little more likely, could be a more complex function eventually
                weightedChoice.add(weight, spawnArea);
            }

            SpawnArea choice = weightedChoice.next();
            return choice; //pick one
        }
        return null;
    }

    //TODO not sure if the built in method for this is any less expensive than checking it ourselves
    private static Location getHighestBlock(Location l, int minY, int maxY) {
        Location highest = l.toBlockLocation();
        highest.setY(maxY);

        while (highest.getBlock().isPassable() && highest.getBlockY() > minY)
            highest.add(0, -1, 0);

        return highest;
    }

    private static List<SpawnArea> findBlockPairs(Location l, int minY, int maxY) {
        if(maxY > 256)
            maxY = 256;
        if(minY < 0)
            minY = 0;
        List<SpawnArea> locations = new ArrayList<>();
        //make a copy of the given location so we don't change its coords.
        Location highest = getHighestBlock(l, minY, maxY);
        if (highest.getBlockY() < maxY) { //if there's a gap with the sky, add the highest block and current
            if (highest.getBlockY() == minY) //if we found void, return an empty list
                return locations;
//            CustomMobsAPI.debug("Highest block: " + highest.getBlockY());
            locations.add(new SpawnArea(new Location(highest.getWorld(), highest.getX(), 256, highest.getZ()), highest.toBlockLocation().add(0, 1, 0)));
        }

        Location searchL = highest.toBlockLocation().add(0, -1, 0); //location for searching downwards
        Location top = searchL; //the top block of a section
        while (searchL.getBlockY() > 0) {
            Block prevBlock = searchL.getBlock();
            searchL = searchL.add(0, -1, 0);
            Block nextBlock = searchL.getBlock();

            if (!prevBlock.isPassable() && nextBlock.isPassable()) //if went from solid to air
                top = searchL.toBlockLocation();
            else if (prevBlock.isPassable() && (!nextBlock.isPassable() || searchL.getBlockY() == 1)) { //if went back to solid or reached the bottom of the world
                SpawnArea temp = new SpawnArea(top, searchL.toBlockLocation().add(0, 1, 0));
                locations.add(temp);
//                CustomMobsAPI.debug("Added location " + temp);
            }
        }
        return locations;
    }


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

            if (manager.isAbyssWorld(p.getWorld())) {
                String layerName = manager.getLayerForSection(worldManager.getSectionFor(p.getLocation())).getName();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        List<MobSpawnEvent> toSpawn = new ArrayList<>();
                        int mobCount = 0;
                        for (Entity e : p.getNearbyEntities(256, 256, 256)) {
                            if (((CraftEntity) e).getHandle() instanceof CustomMob)
                                mobCount++;
                            if (e.getType().equals(EntityType.PLAYER))
                                closePlayers.add(e.getUniqueId());
                        }

                        if (mobCount < MOB_CAP)
                            CustomMobsAPI.debug(ChatColor.LIGHT_PURPLE + "" + mobCount + " mobs before");

                        for (int i = 0; i < MAX_TRIES; i++) {
                            if (i == MAX_TRIES - 1)
                                CustomMobsAPI.debug("Ran out of tries when picking mob spawns");
                            if (mobCount >= MOB_CAP)
                                break;

                            SpawnArea spawnArea = getSpawnArea(p.getLocation(), 25, 50);
                            if (spawnArea == null) {
                                CustomMobsAPI.debug(ChatColor.RED + "getSpawnArea nulled");
                                continue;
                            }
                            //get the list of mob spawns for entity layer, then remove all impossible spawns, and make entity weighted decision on the spawn
                            RandomCollection<MobSpawn> validSpawns = new RandomCollection<>();

                            for (MobSpawn spawn : SpawnRegistry.getLayerSpawns().get(layerName)) {
                                validSpawns.add(spawn.getPriority(spawnArea), spawn);
                            }

                            if (validSpawns.isEmpty()) {
//                                CustomMobsAPI.debug(ChatColor.RED + "Spawn Choice Failed");
                                continue;
                            }
//                            CustomMobsAPI.debug(worldManager.getSectionFor(p.getLocation()).toString());

                            //weighted random decision of valid spawn
                            MobSpawnEvent spawn = new MobSpawnEvent(validSpawns.next(), spawnArea);
                            toSpawn.add(spawn);

                            int spawns = spawn.getSpawns();
                            if (spawns == 0)
                                break;
                            mobCount += spawns;
                        }
                        if (mobCount < MOB_CAP)
                        CustomMobsAPI.debug(ChatColor.LIGHT_PURPLE + "" + mobCount + " after");

                        //after we've hit the mob cap, spawn all the mobs we were planning to
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (MobSpawnEvent spawn : toSpawn)
                                    spawn.spawn();
                            }
                        }.runTask(plugin);
                    }
                }.runTaskAsynchronously(plugin);
            }
        }
    }
}