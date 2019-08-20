package com.offz.spigot.abyssialcreatures;

import com.offz.spigot.mobzy.Mobzy;
import com.offz.spigot.mobzy.MobzyAPI;
import com.offz.spigot.mobzy.MobzyAddon;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AbyssialCreatures extends JavaPlugin implements MobzyAddon {
    @Override
    public void onEnable() {
        getLogger().info("On enable has been called");
        saveDefaultConfig();
        loadTypes();
        MobzyAPI.registerSpawnConfig(new File(getDataFolder(), "spawns.yml"), this);
        JavaPlugin.getPlugin(Mobzy.class).reloadExistingEntities();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Plugin shutdown logic
        getLogger().info("onDisable has been invoked!");
    }

    @Override
    public void loadTypes() {
        MobzyAPI.registerMobConfig(new File(getDataFolder(), "mobs.yml"), this);
        new AbyssialType();
        AbyssialType.registerTypes();
    }
}