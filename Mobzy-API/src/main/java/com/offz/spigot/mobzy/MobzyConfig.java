package com.offz.spigot.mobzy;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.offz.spigot.mobzy.mobs.types.PassiveMob;
import com.offz.spigot.mobzy.spawning.SpawnRegistry;
import net.minecraft.server.v1_15_R1.Entity;
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
    private static int spawnSearchRadius;
    private static int minChunkSpawnRad;
    private static int maxChunkSpawnRad;
    private static int maxSpawnAmount;
    private static int spawnTaskDelay;
    private static Map<Class<? extends Entity>, Integer> mobCaps = new HashMap<>();
    private List<MobzyAddon> registeredAddons = new ArrayList<>();
    private Mobzy plugin;
    private Map<File, FileConfiguration> spawnCfgs = new HashMap<>();
    private Map<File, FileConfiguration> mobCfgs = new HashMap<>();
    private BiMap<String, Class<? extends Entity>> registeredMobTypes = HashBiMap.create();

    public MobzyConfig(Mobzy plugin) {
        this.plugin = plugin;
        reload();
    }

    public BiMap<String, Class<? extends Entity>> getRegisteredMobTypes() {
        return registeredMobTypes;
    }

    public void registerMobType(String name, Class<? extends Entity> type){
        registeredMobTypes.put(name, type);
    }

    /**
     * @return whether custom mob spawning enabled
     */
    public static boolean doMobSpawns() {
        return doMobSpawns;
    }

    /**
     * @param type a specific mob type
     * @return the registered mob cap for that mob
     */
    public static int getMobCap(Class<? extends Entity> type) {
        return mobCaps.get(type);
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
     * @return the maximum number of mobs to spawn with /mobzy spawn
     */
    public static int getMaxSpawnAmount() {
        return maxSpawnAmount;
    }
    /**
     * @return the delay in ticks between each attempted mob spawn
     */
    public static int getSpawnTaskDelay() {
        return spawnTaskDelay;
    }

    public List<MobzyAddon> getRegisteredAddons() {
        return registeredAddons;
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
        spawnSearchRadius = config.getInt("spawnSearchRadius");
        minChunkSpawnRad = config.getInt("minChunkSpawnRad");
        maxChunkSpawnRad = config.getInt("maxChunkSpawnRad");
        maxSpawnAmount = config.getInt("maxSpawnAmount");
        spawnTaskDelay = config.getInt("spawnTaskDelay");

        //TODO make it so other plugins can register mob caps
        //register mob caps
        registeredMobTypes.forEach((name, type) -> mobCaps.put(type, (int) config.get("mobCaps." + name)));
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
        SpawnRegistry.INSTANCE.readCfg(spawnCfgs.get(file));
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
        if (!registeredAddons.contains(plugin))
            registeredAddons.add((MobzyAddon) plugin);
        plugin.getLogger().info(ChatColor.YELLOW + "Registered addons: " + registeredAddons);
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
        CustomType.Companion.getTypes().clear();
        registeredMobTypes.clear();
        plugin.getLogger().info(ChatColor.YELLOW + "Registered addons: " + registeredAddons.toString());

        registerMobType("passive", PassiveMob.class);
//        registerMobType("hostile", HostileMob.class); //FIXME
//        registerMobType("flying", FlyingMob.class);
        registeredAddons.forEach(addon -> addon.registerWithMobzy(plugin));
        loadConfigValues();
        reloadConfigurationMap(mobCfgs);
        SpawnRegistry.INSTANCE.unregisterAll();
        reloadConfigurationMap(spawnCfgs);
    }

    /**
     * @param configs reload the {@link FileConfiguration}s of this passed map
     */
    private void reloadConfigurationMap(Map<File, FileConfiguration> configs) {
        for (File file : configs.keySet()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            configs.put(file, config);
            SpawnRegistry.INSTANCE.readCfg(config);
        }
    }
}
