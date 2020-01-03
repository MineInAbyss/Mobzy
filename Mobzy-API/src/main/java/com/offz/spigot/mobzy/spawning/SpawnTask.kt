package com.offz.spigot.mobzy.spawning;

import com.offz.spigot.mobzy.Mobzy;
import com.offz.spigot.mobzy.MobzyAPIKt;
import com.offz.spigot.mobzy.MobzyConfig;
import com.offz.spigot.mobzy.spawning.vertical.SpawnArea;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.minecraft.server.v1_15_R1.Entity;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

//TODO convert to kotlin
public class SpawnTask extends BukkitRunnable {
    private static final int SPAWN_TRIES = 5;
    private Mobzy plugin;
    private MobzyConfig config;

    public SpawnTask(Mobzy plugin) {
        this.plugin = plugin;
        config = plugin.getMobzyConfig();
    }

    @Override
    public void run() {
        cancel(); //FIXME getNearbyEntities is no longer async
        if (!MobzyConfig.doMobSpawns())
            cancel();

        try {
            //run checks asynchronously
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                List<UUID> skippedPlayers = new ArrayList<>();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    UUID uuid = p.getUniqueId();
                    List<Location> closePlayers = new ArrayList<>();
                    closePlayers.add(p.getLocation());

                    //if this player has been registered as close to another, do not make additional spawns
                    if (skippedPlayers.contains(uuid))
                        continue;

                    //Get WorldGuard regions for the spawn position
                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionManager regions = container.get(BukkitAdapter.adapt(p.getWorld()));
                    if (regions == null)
                        continue;

                    //decide spawns around player
                    List<MobSpawnEvent> toSpawn = new ArrayList<>();
                    Map<Class<? extends net.minecraft.server.v1_15_R1.Entity>, MutableInt> originalMobCount = new HashMap<>();
                    config.getRegisteredMobTypes().values().forEach(type -> originalMobCount.put(type, new MutableInt(0)));
                    MutableInt totalMobs = new MutableInt(0);

                    //go through entities around player, adding nearby players to a list
                    for (org.bukkit.entity.Entity entity : p.getNearbyEntities(MobzyConfig.getSpawnSearchRadius(), MobzyConfig.getSpawnSearchRadius(), MobzyConfig.getSpawnSearchRadius())) {
                        if (MobzyAPIKt.isCustomMob(entity))
                            for (Class<? extends Entity> type : config.getRegisteredMobTypes().values()) {
                                if (type.isInstance(MobzyAPIKt.toNMS(entity))) {
                                    MutableInt count = originalMobCount.get(type);
                                    count.increment();
                                } else if (entity.getType().equals(EntityType.PLAYER)) {
                                    skippedPlayers.add(entity.getUniqueId());
                                    closePlayers.add(entity.getLocation());
                                } else
                                    continue;
                                totalMobs.increment();
                            }
                    }

                    Map<Class<? extends net.minecraft.server.v1_15_R1.Entity>, MutableInt> mobCount = new HashMap<>();
                    originalMobCount.forEach((key, value) -> mobCount.put(key, new MutableInt(value.intValue())));

                    //if we've hit the cap, stop spawning
                    if (mobCount.entrySet().stream().anyMatch((entry) -> entry.getValue().intValue() > MobzyConfig.getMobCap(entry.getKey())))
                        return;

                    //STEP 1: Generate array of ChunkSpawns around player, and invalidate the ones that are empty
                    SpawnChunkGrid spawnChunkGrid = new SpawnChunkGrid(closePlayers, MobzyConfig.getMinChunkSpawnRad(), MobzyConfig.getMaxChunkSpawnRad());
                    //sort by highest preference

                    mobTypeLoop:
                    for (Class<? extends Entity> type : config.getRegisteredMobTypes().values()) {
                        List<ChunkSpawn> chunkSpawns = spawnChunkGrid.getShuffledSpawns();

                        for (ChunkSpawn chunkSpawn : chunkSpawns) {
                            //if mob cap of that specific mob has been reached, skip it
                            if (mobCount.get(type).intValue() > MobzyConfig.getMobCap(type))
                                continue mobTypeLoop;

                            //STEP 2: Each chunk tries to choose one area inside it for which to attempt a spawn
                            SpawnArea spawnArea = chunkSpawn.getSpawnArea(SPAWN_TRIES);
                            if (spawnArea == null)
                                continue;


                            //TODO Figure out something for determining the region in different spots in spawn area.
                            // It'll need each mob to decide on a position first, then run it through here.
                            // Maybe this entire system needs reworking

                            //STEP 3: Pick mob to spawn
                            // get the list of mob spawns based on WorldGuard regions, then remove all impossible spawns,
                            // and make entity weighted decision on the spawn
                            Set<ProtectedRegion> inRegions = regions.getApplicableRegions(BukkitAdapter.asBlockVector(spawnArea.getBottom())).getRegions();

                            //If any of the overlapping regions is set to override, the highest priority one will set only its spawns as viable
                            inRegions.stream().sorted()
                                    .filter(region -> region.getFlags().containsKey(Mobzy.getMZ_SPAWN_OVERLAP()) && region.getFlag(Mobzy.getMZ_SPAWN_OVERLAP()).equals("override"))
                                    .findFirst()
                                    .ifPresent(region -> {
                                        inRegions.clear();
                                        inRegions.add(region);
                                    });

                            List<String> regionIDs = inRegions.stream()
                                    .filter(region -> region.getFlags().containsKey(Mobzy.getMZ_SPAWN_REGIONS()))
                                    .map(region -> Arrays.asList(region.getFlag(Mobzy.getMZ_SPAWN_REGIONS()).split(",")))
                                    .flatMap(Collection::stream)
                                    .collect(Collectors.toList());

                            RandomCollection<MobSpawn> validSpawns = new RandomCollection<>();

                            List<MobSpawn> regionSpawns = SpawnRegistry.INSTANCE.getMobSpawnsForRegions(regionIDs, type);

                            if (regionSpawns == null)
                                continue;

                            regionSpawns.forEach(mobSpawn -> validSpawns.add(mobSpawn.getPriority(spawnArea, toSpawn), mobSpawn));

                            if (validSpawns.isEmpty())
                                continue;

                            //weighted random decision of valid spawn
                            MobSpawnEvent spawn = new MobSpawnEvent(validSpawns.next(), spawnArea);
                            toSpawn.add(spawn);

                            //increment the number of existing mobs by the number we want to spawn
                            int spawns = spawn.getSpawns();
                            mobCount.get(type).add(spawns);
                        }
                    }

                    //spawn all the mobs we were planning to
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        for (MobSpawnEvent spawn : toSpawn)
                            spawn.spawn();

                        //after we've hit the mob cap, print mob count
                        if (toSpawn.size() > 0) {
                            MobzyAPIKt.debug(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + (totalMobs + " mobs before"));
                            mobCount.entrySet().stream().filter(entry -> entry.getValue().intValue() - originalMobCount.get(entry.getKey()).intValue() > 0)
                                    .forEach(entry -> MobzyAPIKt.debug(ChatColor.LIGHT_PURPLE + (entry.getValue().intValue() - originalMobCount.get(entry.getKey()).intValue() + " " + config.getRegisteredMobTypes().inverse().get(entry.getKey()) + " mobs spawned")));
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