package com.offz.spigot.mobzy;

import com.offz.spigot.mobzy.Spawning.SpawnRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobzyConfig {
    private static boolean debug;
    private static boolean doMobSpawns;
    private static int passiveMobCap;
    private static int hostileMobCap;
    private static int flyingMobCap;
    private static int spawnSearchRadius;
    private static int minChunkSpawnRad;
    private static int maxChunkSpawnRad;
    private List<MobzyAddon> registeredAddons = new ArrayList<>();
    private Mobzy plugin;
    private Map<File, FileConfiguration> spawnCfgs = new HashMap<>();
    private Map<File, FileConfiguration> mobCfgs = new HashMap<>();
    public MobzyConfig(Mobzy plugin) {
        this.plugin = plugin;
        reload();
    }

    /**
     * @return whether custom mob spawning enabled
     */
    public static boolean doMobSpawns() {
        return doMobSpawns;
    }

    /**
     * @return the passive mob cap
     * TODO eventually these should just be a map of maps that custom plugins can add to
     */
    public static int getPassiveMobCap() {
        return passiveMobCap;
    }

    /**
     * @return the hostile mob cap
     */
    public static int getHostileMobCap() {
        return hostileMobCap;
    }

    /**
     * @return the flying mob cap
     */
    public static int getFlyingMobCap() {
        return flyingMobCap;
    }

    /**
     * @return the radius around which players will count mobs towards the local mob cap
     */
    public static int getSpawnSearchRadius() {
        return spawnSearchRadius;
    }

    /**
     * @return the minimum number of chunks away from the player in which a mob can spawn
     */
    public static int getMinChunkSpawnRad() {
        return minChunkSpawnRad;
    }

    /**
     * @return the maximum number of chunks away from the player in which a mob can spawn
     */
    public static int getMaxChunkSpawnRad() {
        return maxChunkSpawnRad;
    }


    /**
     * @return whether the plugin is in a debug state (used primarily for broadcasting messages)
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Reads the configuration values from the plugin's config.yml file
     */
    private void loadConfigValues() {
        FileConfiguration config = plugin.getConfig();

        debug = config.getBoolean("debug");
        doMobSpawns = config.getBoolean("doMobSpawns");
        passiveMobCap = config.getInt("passiveMobCap");
        hostileMobCap = config.getInt("hostileMobCap");
        flyingMobCap = config.getInt("flyingMobCap");
        spawnSearchRadius = config.getInt("spawnSearchRadius");
        minChunkSpawnRad = config.getInt("minChunkSpawnRad");
        maxChunkSpawnRad = config.getInt("maxChunkSpawnRad");
    }

    public Map<File, FileConfiguration> getMobCfgs() {
        return mobCfgs;
    }

    public Map<File, FileConfiguration> getSpawnCfgs() {
        return spawnCfgs;
    }

    /**
     * Registers a {@link FileConfiguration} of spawns to be used by the plugin.
     *
     * @param file   the file to be read from
     * @param plugin the plugin this file corresponds to
     */
    void registerSpawnCfg(File file, JavaPlugin plugin) {
        registerCfg(spawnCfgs, file, plugin);
        SpawnRegistry.readCfg(spawnCfgs.get(file));
    }

    /**
     * Registers a {@link FileConfiguration} of mobs, describing their attributes, such as model to be used
     *
     * @param file   the file to be read from
     * @param plugin the plugin this file corresponds to
     */
    void registerMobCfg(File file, JavaPlugin plugin) {
        registerCfg(mobCfgs, file, plugin);
        //TODO do static fields get kept after plugin reload?
        registeredAddons.add((MobzyAddon) plugin);
    }

    private void registerCfg(Map<File, FileConfiguration> config, File file, JavaPlugin plugin) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Registering configuration " + file.getName());
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(file.getName(), false);
            plugin.getLogger().info(ChatColor.GREEN + file.getName() + " has been created");
        }
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        config.put(file, configuration);
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + config.toString());
    }

    public void saveSpawnCfg(FileConfiguration config) {
        Map.Entry<File, FileConfiguration> configEntry = spawnCfgs.entrySet().stream()
                .filter(entry -> entry.getValue().equals(config))
                .findFirst()
                .get();
        try {
            configEntry.getValue().save(configEntry.getKey());

            plugin.getLogger().info(ChatColor.AQUA + "Spawns file has been saved");
        } catch (IOException e) {
            plugin.getLogger().info(ChatColor.RED + "Could not save the spawns file");
            e.printStackTrace();
        }
    }

    /**
     * Reload the configurations stored in the plugin. Most stuff requires a full reload of the plugin now
     * TODO make it properly reload everything
     */
    public void reload() {
        SpawnRegistry.unregisterAll();
        CustomType.getTypes().clear();
        Bukkit.broadcastMessage(registeredAddons.toString());
        registeredAddons.forEach(MobzyAddon::loadTypes);
        loadConfigValues();
        reloadConfigurationMap(spawnCfgs);
        reloadConfigurationMap(mobCfgs);
    }

    /**
     * @param configs reload the {@link FileConfiguration}s of this passed map
     */
    private void reloadConfigurationMap(Map<File, FileConfiguration> configs) {
        for (File file : configs.keySet()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            configs.put(file, config);
            SpawnRegistry.readCfg(config);
        }
    }
}
