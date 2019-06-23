package com.offz.spigot.custommobs.Spawning;

import com.derongan.minecraft.deeperworld.world.WorldManager;
import com.derongan.minecraft.mineinabyss.AbyssContext;
import com.derongan.minecraft.mineinabyss.world.AbyssWorldManager;
import com.offz.spigot.custommobs.CustomMobs;
import com.offz.spigot.custommobs.CustomMobsAPI;
import com.offz.spigot.custommobs.Loading.SpawnRegistry;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import com.offz.spigot.custommobs.Spawning.Vertical.SpawnArea;
import com.offz.spigot.custommobs.Spawning.Vertical.VerticalSpawn;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SpawnTask extends BukkitRunnable {
    private final int MOB_CAP = 20;
    private final int MAX_TRIES = 100;
    private AbyssContext context;
    private WorldManager worldManager;
    private CustomMobs plugin;

    public SpawnTask(CustomMobs plugin, AbyssContext context) {
        this.context = context;
        this.worldManager = context.getRealWorldManager();
        this.plugin = plugin;
    }

    //TODO cleanup/remove
    public static Location getSpawnInRadius(Location p, double minRad, double maxRad) {
        for (int i = 0; i < 30; i++) {
            double y = (Math.random() - 0.5) * maxRad;
            if (Math.abs(y) > minRad) //if y is minRad blocks away from player, mobs can spawn directly under or above
                minRad = 0;

            double x = Math.signum(Math.random() - 0.5) * ((Math.random() * (maxRad - minRad)) + minRad);
            double z = Math.signum(Math.random() - 0.5) * ((Math.random() * (maxRad - minRad)) + minRad);
            if (!p.isChunkLoaded())
                return null;
            Location l = p.clone();
            l = l.add(new Vector(x, y, z));

            if (!l.getBlock().getType().isSolid()) {
                l = VerticalSpawn.checkDown(l, 25);
                if (l != null)
                    return l;
            } else {
                l = VerticalSpawn.checkUp(l, 25);
                if (l != null)
                    return l;
            }
        }
        return null;
    }

    private static List<ChunkSpawn> generateChunkArray(Location loc, int minRad, int maxRad) {
        List<ChunkSpawn> chunkSpawns = new ArrayList<>();
        Chunk chunk = loc.getChunk();
        int startX = chunk.getX();
        int startZ = chunk.getZ();
        World world = loc.getWorld();

        //add chunks in a circle around player to the list
        for (int x = -maxRad; x <= maxRad; x++) {
            for (int z = -maxRad; z <= maxRad; z++) {
                double dist = x * x + z * z;
                //if we are within the maximum circular radius and not within minimum, add the chunk to the list
                if (dist <= maxRad * maxRad) {
                    Chunk spawnChunk = world.getChunkAt(startX + x, startZ + z);
                    if (dist > minRad * minRad) {
                        ChunkSpawn chunkSpawn = new ChunkSpawn(spawnChunk, 0, 254);
                        if (chunkSpawn.getPreference() > 0) //add the chunk if we like its spawn chances
                            chunkSpawns.add(chunkSpawn);
                    } else {
                        int minVertical = minRad * 16; //minimum vertical spawn distance is the number of chunks * width of chunks
                        //do some checks to add areas above and below the player when we've reached inside the minimum radius
                        if (loc.getY() + minVertical < 254) {
                            ChunkSpawn topSpawn = new ChunkSpawn(spawnChunk, ((int) loc.getY() + minVertical), 254);
                            if (topSpawn.getPreference() > 0)
                                chunkSpawns.add(topSpawn);
                        }
                        if (loc.getY() - minVertical > 0) {
                            ChunkSpawn bottomSpawn = new ChunkSpawn(spawnChunk, 0, ((int) loc.getY() - minVertical));
                            if (bottomSpawn.getPreference() > 0)
                                chunkSpawns.add(bottomSpawn);
                        }
                    }
                }
            }
        }
        return chunkSpawns;
    }

    @Override
    public void run() {
        AbyssWorldManager manager = context.getWorldManager();
        List<UUID> closePlayers = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uuid = p.getUniqueId();

            //if this player has been registered as close to another, do not make additional spawns
            if (closePlayers.contains(uuid)) {
                closePlayers.remove(uuid);
                continue;
            }

            if (!manager.isAbyssWorld(p.getWorld())) {
                continue;
            }

            String layerName = manager.getLayerForSection(worldManager.getSectionFor(p.getLocation())).getName();

            //decide spawns around player asynchronously
            new BukkitRunnable() {
                @Override
                public void run() {
                    List<MobSpawnEvent> toSpawn = new ArrayList<>();
                    int mobCount = 0;

                    //go through entities around player, adding nearby players to a list
                    for (Entity e : p.getNearbyEntities(256, 256, 256)) {
                        if (((CraftEntity) e).getHandle() instanceof CustomMob)
                            mobCount++;
                        //if a player was nearby, add them to the close players list so we don't overlap spawns
                        if (e.getType().equals(EntityType.PLAYER))
                            closePlayers.add(e.getUniqueId());
                    }

                    if (mobCount >= MOB_CAP)
                        return;
                    CustomMobsAPI.debug(ChatColor.LIGHT_PURPLE + (mobCount + " mobs before"));

                    //STEP 1: Generate array of ChunkSpawns around player, and invalidate the ones that are empty
                    List<ChunkSpawn> chunkSpawns = generateChunkArray(p.getLocation(), 2, 4);
                    //sort by highest preference
                    Collections.sort(chunkSpawns);

                    for (ChunkSpawn chunkSpawn : chunkSpawns) {
                        if (mobCount >= MOB_CAP)
                            break;

                        //STEP 2: Each chunk tries to choose one area inside it for which to attempt a spawn
                        SpawnArea spawnArea = chunkSpawn.getSpawnArea(3);
                        if (spawnArea == null) {
//                            CustomMobsAPI.debug(ChatColor.RED + "getSpawnArea nulled");
                            continue;
                        }

                        //STEP 3: Pick mob to spawn
                        //get the list of mob spawns for entity layer, then remove all impossible spawns,
                        //and make entity weighted decision on the spawn
                        RandomCollection<MobSpawn> validSpawns = new RandomCollection<>();

                        for (MobSpawn spawn : SpawnRegistry.getLayerSpawns().get(layerName)) {
                            validSpawns.add(spawn.getPriority(spawnArea), spawn);
                        }

                        if (validSpawns.isEmpty()) {
//                                CustomMobsAPI.debug(ChatColor.RED + "Spawn Choice Failed");
                            continue;
                        }

                        //weighted random decision of valid spawn
                        MobSpawnEvent spawn = new MobSpawnEvent(validSpawns.next(), spawnArea);
                        toSpawn.add(spawn);

                        int spawns = spawn.getSpawns();
                        mobCount += spawns;
                    }

                    //after we've hit the mob cap, print mob count
                    if (toSpawn.size() > 0)
                        CustomMobsAPI.debug(ChatColor.LIGHT_PURPLE + (mobCount + " mobs after"));

                    //spawn all the mobs we were planning to
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