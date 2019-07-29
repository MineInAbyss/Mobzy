package com.offz.spigot.mobzy;

import com.offz.spigot.mobzy.Spawning.SpawnRegistry;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private Mobzy plugin = Mobzy.getPlugin(Mobzy.class);

    private Map<File, FileConfiguration> spawnCfgs = new HashMap<>();

    //TODO turn into map as well
    private Map<File, FileConfiguration> mobCfgs = new HashMap<>();
    private File mobsFile;

    public Map<File, FileConfiguration> getMobCfgs() {
        return mobCfgs;
    }

    /**
     * Registers a {@link FileConfiguration} of spawns to be used by the plugin.
     *
     * @param file the file to be read from
     * @param plugin the plugin this file corresponds to
     */
    void registerSpawnCfg(File file, JavaPlugin plugin) {
        registerCfg(spawnCfgs, ChatColor.GREEN + file.getName() + " has been created for spawn configuration", file, plugin);
        SpawnRegistry.readCfg(spawnCfgs.get(file));
    }

    void registerMobCfg(File file, JavaPlugin plugin) {
        registerCfg(spawnCfgs, ChatColor.GREEN + file.getName() + " has been created for mob configuration", file, plugin);
    }

    private void registerCfg(Map<File, FileConfiguration> config, String loadMsg, File file, JavaPlugin plugin){
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(file.getName(), false);
            plugin.getLogger().info(loadMsg);
        }
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        config.put(file, configuration);
    }

    void setup() {
        mobsFile = new File(plugin.getDataFolder(), "mobs.yml");

        if (!mobsFile.exists()) {
            mobsFile.getParentFile().mkdirs();
            plugin.saveResource("mobs.yml", false);
            plugin.getLogger().info(ChatColor.GREEN + "The mobs.yml file has been created");
        }
        reload();
    }

    @Deprecated
    public void saveSpawns() {
        try {
            for (Map.Entry<File, FileConfiguration> entry : spawnCfgs.entrySet())
                entry.getValue().save(entry.getKey());

            plugin.getLogger().info(ChatColor.AQUA + "Spawns files have been saved");
        } catch (IOException e) {
            plugin.getLogger().info(ChatColor.RED + "Could not save the spawns files");
            e.printStackTrace();
        }
    }

    /**
     * Reload the configurations stored in the plugin. Most stuff requires a full reload of the plugin now
     * TODO make it properly reload everything
     */
    public void reload() {
        SpawnRegistry.unregisterAll();
        CustomMobsAPI.loadConfigValues(plugin);
        reloadConfigurationMap(spawnCfgs);
        reloadConfigurationMap(mobCfgs);
    }

    /**
     * @param cfgs relod the {@link FileConfiguration}s of this passed map
     */
    private void reloadConfigurationMap(Map<File, FileConfiguration> cfgs){
        for (File file : cfgs.keySet()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            cfgs.put(file, config);
            SpawnRegistry.readCfg(config);
        }
    }
}
