package com.offz.spigot.abyssalcreatures;

import com.offz.spigot.mobzy.Mobzy;
import com.offz.spigot.mobzy.MobzyAPI;
import com.offz.spigot.mobzy.MobzyAddon;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AbyssalCreatures extends JavaPlugin implements MobzyAddon {
    @Override
    public void onEnable() {
        getLogger().info("On enable has been called");
        saveDefaultConfig();
        registerWithMobzy(Mobzy.getInstance());
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Plugin shutdown logic
        getLogger().info("onDisable has been invoked!");
    }

    @Override
    public void registerWithMobzy(Mobzy mobzy) {
        MobzyAPI.registerMobConfig(new File(getDataFolder(), "mobs.yml"), this);
        new AbyssalType();
        AbyssalType.registerTypes();

        MobzyAPI.registerSpawnConfig(new File(getDataFolder(), "spawns.yml"), this);
        mobzy.reloadExistingEntities();
    }
}