package com.offz.spigot.custommobs;

import com.offz.spigot.custommobs.Spawning.SpawnRegistry;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private CustomMobs plugin = CustomMobs.getPlugin(CustomMobs.class);

    private FileConfiguration spawnCfg;
    private File spawnFile;

    private FileConfiguration mobsCfg;
    private File mobsFile;

    public FileConfiguration getMobsCfg() {
        return mobsCfg;
    }

    public FileConfiguration getSpawnCfg() {
        return spawnCfg;
    }

    public void setup() {
        if (plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();

        spawnFile = new File(plugin.getDataFolder(), "spawns.yml");

        mobsFile = new File(plugin.getDataFolder(), "mobs.yml");

        if (!spawnFile.exists()) {
            spawnFile.getParentFile().mkdirs();
            plugin.saveResource("spawns.yml", false);
            plugin.getLogger().info(ChatColor.GREEN + "The spawns.yml file has been created");
        }
        if (!mobsFile.exists()) {
            mobsFile.getParentFile().mkdirs();
            plugin.saveResource("mobs.yml", false);
            plugin.getLogger().info(ChatColor.GREEN + "The mobs.yml file has been created");
        }

        reload();
    }

    public void saveSpawns() {
        try {
            spawnCfg.save(spawnFile);
            plugin.getLogger().info(ChatColor.AQUA + "The spawns.yml file has been saved");
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().info(ChatColor.RED + "Could not save the spawns.yml file");
        }
    }

    public void reload() {
        spawnCfg = YamlConfiguration.loadConfiguration(spawnFile);
        mobsCfg = YamlConfiguration.loadConfiguration(mobsFile);
        SpawnRegistry.readCfg(this);
        CustomMobsAPI.loadConfigValues(plugin);
    }
}
