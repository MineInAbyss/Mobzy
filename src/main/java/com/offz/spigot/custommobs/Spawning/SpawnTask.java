package com.offz.spigot.custommobs.Spawning;

import com.offz.spigot.custommobs.CustomMobs;
import com.offz.spigot.custommobs.CustomMobsAPI;
import com.offz.spigot.custommobs.Mobs.Types.FlyingMob;
import com.offz.spigot.custommobs.Mobs.Types.HostileMob;
import com.offz.spigot.custommobs.Mobs.Types.PassiveMob;
import com.offz.spigot.custommobs.Spawning.Vertical.SpawnArea;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class SpawnTask extends BukkitRunnable {
    private CustomMobs plugin;
    private static final int SPAWN_TRIES = 5;

    public SpawnTask(CustomMobs plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!CustomMobsAPI.doMobSpawns())
            cancel();

        try {
            //run checks asynchronously
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                List<UUID> closePlayers = new ArrayList<>();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    UUID uuid = p.getUniqueId();

                    //if this player has been registered as close to another, do not make additional spawns
                    if (closePlayers.contains(uuid)) {
//                closePlayers.remove(uuid);
                        continue;
                    }

                    //Get WorldGuard regions for the spawn position
                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionManager regions = container.get(BukkitAdapter.adapt(p.getWorld()));
                    if (regions == null)
                        continue;

//                    String layerName = manager.getLayerForSection(worldManager.getSectionFor(p.getLocation())).getName();

                    //decide spawns around player
//                try {
                    List<MobSpawnEvent> toSpawn = new ArrayList<>();
                    //0 = passive, 1 = hostile, 2 = flying
                    int[] originalMobCount = new int[3];

                    //go through entities around player, adding nearby players to a list
                    for (Entity e : p.getNearbyEntities(CustomMobsAPI.getSpawnSearchRadius(), CustomMobsAPI.getSpawnSearchRadius(), CustomMobsAPI.getSpawnSearchRadius())) {
                        if (((CraftEntity) e).getHandle() instanceof PassiveMob)
                            originalMobCount[0]++;
                        if (((CraftEntity) e).getHandle() instanceof HostileMob)
                            originalMobCount[1]++;
                        if (((CraftEntity) e).getHandle() instanceof FlyingMob)
                            originalMobCount[2]++;
                        //if a player was nearby, add them to the close players list so we don't overlap spawns
                        if (e.getType().equals(EntityType.PLAYER) && e.getLocation().distance(p.getLocation()) < 30)
                            closePlayers.add(e.getUniqueId());
                    }
                    int[] mobCount = Arrays.copyOf(originalMobCount, 3);
                    int totalMobs = 0;
                    for (int mobs : mobCount)
                        totalMobs += mobs;

                    //if we've hit the cap, stop spawning
                    if (mobCount[0] >= CustomMobsAPI.getPassiveMobCap() && mobCount[1] >= CustomMobsAPI.getHostileMobCap() && mobCount[2] >= CustomMobsAPI.getFlyingMobCap())
                        return;

                    //STEP 1: Generate array of ChunkSpawns around player, and invalidate the ones that are empty
                    SpawnChunkGrid spawnChunkGrid = new SpawnChunkGrid(p.getLocation(), CustomMobsAPI.getMinChunkSpawnRad(), CustomMobsAPI.getMaxChunkSpawnRad());
                    //sort by highest preference

                    mobTypeLoop:
                    for (int mobType = 0; mobType < 3; mobType++) {
                        List<ChunkSpawn> chunkSpawns = spawnChunkGrid.getShuffledSpawns();

                        for (ChunkSpawn chunkSpawn : chunkSpawns) {
                            //if mob cap of that specific mob has been reached, skip it
                            switch (mobType) {
                                case 0:
                                    if (mobCount[0] >= CustomMobsAPI.getPassiveMobCap())
                                        continue mobTypeLoop;
                                    break;
                                case 1:
                                    if (mobCount[1] >= CustomMobsAPI.getHostileMobCap())
                                        continue mobTypeLoop;
                                    break;
                                case 2:
                                    if (mobCount[2] >= CustomMobsAPI.getFlyingMobCap())
                                        continue mobTypeLoop;
                                    break;
                            }

                            //STEP 2: Each chunk tries to choose one area inside it for which to attempt a spawn
                            SpawnArea spawnArea = chunkSpawn.getSpawnArea(SPAWN_TRIES);
                            if (spawnArea == null)
                                continue;

                            //STEP 3: Pick mob to spawn
                            //get the list of mob spawns based on WorldGuard regions, then remove all impossible spawns,
                            //and make entity weighted decision on the spawn

                            //TODO Figure out something for determining the region in different spots in spawn area.
                            // It'll need each mob to decide on a position first, then run it through here.
                            // Maybe this entire system needs reworking
                            Set<ProtectedRegion> inRegions = regions.getApplicableRegions(BukkitAdapter.asBlockVector(spawnArea.getBottom())).getRegions();
                            List<String> regionIDs = inRegions.stream()
                                    .filter(region -> region.getFlags().containsKey(CustomMobs.CM_SPAWN_REGIONS))
                                    .map(region -> Arrays.asList(region.getFlag(CustomMobs.CM_SPAWN_REGIONS).split(",")))
                                    .flatMap(Collection::stream)
                                    .collect(Collectors.toList());

                            RandomCollection<MobSpawn> validSpawns = new RandomCollection<>();

                            List<MobSpawn> regionSpawns = SpawnRegistry.getMobSpawnsForRegions(regionIDs, mobType);

                            if (regionSpawns == null)
                                continue;

                            regionSpawns.forEach(mobSpawn -> validSpawns.add(mobSpawn.getPriority(spawnArea), mobSpawn));

                            if (validSpawns.isEmpty())
                                continue;

                            //weighted random decision of valid spawn
                            MobSpawnEvent spawn = new MobSpawnEvent(validSpawns.next(), spawnArea);
                            toSpawn.add(spawn);

                            int spawns = spawn.getSpawns();
                            mobCount[mobType] += spawns;
                        }
                    }

                    //spawn all the mobs we were planning to
                    int finalTotalMobs = totalMobs;
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        for (MobSpawnEvent spawn : toSpawn)
                            spawn.spawn();

                        //after we've hit the mob cap, print mob count
                        if (toSpawn.size() > 0) {
                            CustomMobsAPI.debug(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + (finalTotalMobs + " mobs before"));
                            if (mobCount[0] - originalMobCount[0] > 0)
                                CustomMobsAPI.debug(ChatColor.LIGHT_PURPLE + (mobCount[0] - originalMobCount[0] + " passive mobs spawned"));
                            if (mobCount[1] - originalMobCount[1] > 0)
                                CustomMobsAPI.debug(ChatColor.LIGHT_PURPLE + (mobCount[1] - originalMobCount[1] + " hostile mobs spawned"));
                            if (mobCount[2] - originalMobCount[2] > 0)
                                CustomMobsAPI.debug(ChatColor.LIGHT_PURPLE + (mobCount[2] - originalMobCount[2] + " flying mobs spawned"));
                        }
                    });
                }
            });
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
            cancel();
        }
    }
}