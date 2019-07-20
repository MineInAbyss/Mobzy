package com.offz.spigot.custommobs;

import com.offz.spigot.custommobs.Spawning.SpawnRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private CustomMobs plugin = CustomMobs.getPlugin(CustomMobs.class);

    private FileConfiguration spawnCfg;
    private File spawnFile;

    public FileConfiguration getSpawnCfg() {
        return spawnCfg;
    }

    public void setup() {
        if (plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();

        spawnFile = new File(plugin.getDataFolder(), "spawns.yml");

        if (!spawnFile.exists()) {
            try {
                spawnFile.createNewFile();
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "The spawns.yml file has been created");
            } catch (IOException e) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not create the players.yml file");
            }
        }

        spawnCfg = YamlConfiguration.loadConfiguration(spawnFile);
        SpawnRegistry.readCfg(this);
    }

    public void saveSpawns() {
        try {
            spawnCfg.save(spawnFile);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "The spawns.yml file has been saved");
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the spawns.yml file");
        }
    }

    public void reload() {
        spawnCfg = YamlConfiguration.loadConfiguration(spawnFile);
        SpawnRegistry.readCfg(this);
    }
}
