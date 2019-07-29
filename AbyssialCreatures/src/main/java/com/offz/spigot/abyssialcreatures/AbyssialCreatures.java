package com.offz.spigot.abyssialcreatures;

import com.offz.spigot.mobzy.CustomMobsAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AbyssialCreatures extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("On enable has been called");
        saveDefaultConfig();
        CustomMobsAPI.registerSpawns(new File(getDataFolder(), "spawns.yml"), this);
        CustomMobsAPI.registerMobConfig(new File(getDataFolder(), "mobs.yml"), this);
        AbyssialType.registerTypes();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Plugin shutdown logic
        getLogger().info("onDisable has been invoked!");
    }
}