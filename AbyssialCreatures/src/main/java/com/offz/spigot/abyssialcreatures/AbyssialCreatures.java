package com.offz.spigot.abyssialcreatures;

import com.offz.spigot.mobzy.MobzyAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AbyssialCreatures extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("On enable has been called");
        saveDefaultConfig();
        MobzyAPI.registerMobConfig(new File(getDataFolder(), "mobs.yml"), this);
        new AbyssialType();
        AbyssialType.registerTypes();
        MobzyAPI.registerSpawnConfig(new File(getDataFolder(), "spawns.yml"), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Plugin shutdown logic
        getLogger().info("onDisable has been invoked!");
    }
}