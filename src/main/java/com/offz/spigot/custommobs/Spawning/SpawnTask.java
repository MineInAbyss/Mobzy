package com.offz.spigot.custommobs.Spawning;

import com.derongan.minecraft.deeperworld.world.WorldManager;
import com.derongan.minecraft.mineinabyss.AbyssContext;
import com.derongan.minecraft.mineinabyss.world.AbyssWorldManager;
import com.offz.spigot.custommobs.CustomMobs;
import com.offz.spigot.custommobs.CustomMobsAPI;
import com.offz.spigot.custommobs.Mobs.Flying.FlyingMob;
import com.offz.spigot.custommobs.Mobs.Hostile.HostileMob;
import com.offz.spigot.custommobs.Mobs.Passive.PassiveMob;
import com.offz.spigot.custommobs.Spawning.Vertical.SpawnArea;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SpawnTask extends BukkitRunnable {
    private final int PASSIVE_MOB_CAP = 20;
    private final int HOSTILE_MOB_CAP = 20;
    private final int FLYING_MOB_CAP = 5;
    private final int MAX_TRIES = 100;
    private AbyssContext context;
    private WorldManager worldManager;
    private CustomMobs plugin;

    public SpawnTask(CustomMobs plugin, AbyssContext context) {
        this.context = context;
        this.worldManager = context.getRealWorldManager();
        this.plugin = plugin;
    }

    @Override
    public void run() {
        //run checks asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            AbyssWorldManager manager = context.getWorldManager();
            List<UUID> closePlayers = new ArrayList<>();

            for (Player p : Bukkit.getOnlinePlayers()) {
                UUID uuid = p.getUniqueId();

                //if this player has been registered as close to another, do not make additional spawns
                if (closePlayers.contains(uuid)) {
//                closePlayers.remove(uuid);
                    continue;
                }

                if (!manager.isAbyssWorld(p.getWorld())) {
                    continue;
                }

                String layerName = manager.getLayerForSection(worldManager.getSectionFor(p.getLocation())).getName();

                //decide spawns around player
//                try {
                List<MobSpawnEvent> toSpawn = new ArrayList<>();
                //0 = passive, 1 = hostile, 2 = flying
                int[] originalMobCount = new int[3];

                //go through entities around player, adding nearby players to a list
                for (Entity e : p.getNearbyEntities(256, 256, 256)) {
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
                if (mobCount[0] >= PASSIVE_MOB_CAP && mobCount[1] >= HOSTILE_MOB_CAP && mobCount[2] >= FLYING_MOB_CAP)
                    return;

                //STEP 1: Generate array of ChunkSpawns around player, and invalidate the ones that are empty
                SpawnChunkGrid spawnChunkGrid = new SpawnChunkGrid(p.getLocation(), 2, 4);
                //sort by highest preference

                mobTypeLoop:
                for (int mobType = 0; mobType < 3; mobType++) {
                    List<ChunkSpawn> chunkSpawns = spawnChunkGrid.getShuffledSpawns();

                    for (ChunkSpawn chunkSpawn : chunkSpawns) {
                        //if mob cap of that specific mob has been reached, skip it
                        switch (mobType) {
                            case 0:
                                if (mobCount[0] >= PASSIVE_MOB_CAP)
                                    continue mobTypeLoop;
                                break;
                            case 1:
                                if (mobCount[1] >= HOSTILE_MOB_CAP)
                                    continue mobTypeLoop;
                                break;
                            case 2:
                                if (mobCount[2] >= FLYING_MOB_CAP)
                                    continue mobTypeLoop;
                                break;
                        }

                        //STEP 2: Each chunk tries to choose one area inside it for which to attempt a spawn
                        SpawnArea spawnArea = chunkSpawn.getSpawnArea(3);
                        if (spawnArea == null)
                            continue;

                        //STEP 3: Pick mob to spawn
                        //get the list of mob spawns for entity layer, then remove all impossible spawns,
                        //and make entity weighted decision on the spawn
                        RandomCollection<MobSpawn> validSpawns = new RandomCollection<>();

                        for (MobSpawn spawn : SpawnRegistry.getLayerSpawns().get(layerName).getSpawnsFor(mobType)) {
//                            if(mobType == 1)
//                                CustomMobsAPI.debug("Hostile priority: " + spawn.getPriority(spawnArea));
                            validSpawns.add(spawn.getPriority(spawnArea), spawn);
                        }

                        if (validSpawns.isEmpty()) {
                            continue;
                        }

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
    }
}