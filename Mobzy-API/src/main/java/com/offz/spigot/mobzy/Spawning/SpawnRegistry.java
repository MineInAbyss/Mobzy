package com.offz.spigot.mobzy.Spawning;

import com.offz.spigot.mobzy.CustomType;
import com.offz.spigot.mobzy.Mobzy;
import com.offz.spigot.mobzy.Spawning.Regions.SpawnRegion;
import net.minecraft.server.v1_13_R2.Entity;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpawnRegistry {
    private static Map<String, SpawnRegion> regionSpawns = new HashMap<>();

    public static void unregisterAll() {
        regionSpawns.clear();
    }

    public static void readCfg(FileConfiguration config) {
        List<Map<?, ?>> regionList = config.getMapList("regions");
        Mobzy plugin = Mobzy.getPlugin(Mobzy.class);

        for (Map<?, ?> region : regionList) {
            String name = (String) region.get("name");
            SpawnRegion spawnRegion = new SpawnRegion(name);
            try {
                List<Map<?, ?>> spawnList = (List<Map<?, ?>>) region.get("spawns");

                for (Map<?, ?> spawn : spawnList) {
                    MobSpawn.Builder mobSpawn = MobSpawn.newBuilder();

                    //create a new builder from the MobSpawn found inside of an already created layer
                    if (spawn.containsKey("reuse")) {
                        String reusedMob = (String) spawn.get("reuse");
                        mobSpawn = MobSpawn.newBuilder(getReusedBuilder(reusedMob));
                    }
                    //required information
                    else if (!spawn.containsKey("mob"))
                        break;

                    MobSpawn.deserialize((Map<String, Object>) spawn, mobSpawn);
                    spawnRegion.addSpawn(mobSpawn.build());
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                plugin.getLogger().info(ChatColor.RED + "Skipped region in spawns.yml because of misformatted config");
            }
            regionSpawns.put(name, spawnRegion);
        }
        plugin.getLogger().info(ChatColor.GREEN + "Reloaded spawns.yml");
    }

    public static MobSpawn getReusedBuilder(String reusedMob) {
        return getRegionSpawns().get(reusedMob.substring(0, reusedMob.indexOf(':'))).getSpawnOfType(CustomType.getType(reusedMob.substring(reusedMob.indexOf(':') + 1)));
    }

    public static Map<String, SpawnRegion> getRegionSpawns() {
        return regionSpawns;
    }

    public static List<MobSpawn> getMobSpawnsForRegions(List<String> regionIDs, Class<? extends Entity> mobType) {
        return regionIDs.stream()
                .filter(name -> regionSpawns.containsKey(name))
                .map(name -> regionSpawns.get(name).getSpawnsFor(mobType))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
