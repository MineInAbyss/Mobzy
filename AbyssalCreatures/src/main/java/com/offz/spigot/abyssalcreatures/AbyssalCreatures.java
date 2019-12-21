package com.offz.spigot.abyssalcreatures;

import com.offz.spigot.mobzy.Mobzy;
import com.offz.spigot.mobzy.MobzyAPIKt;
import com.offz.spigot.mobzy.MobzyAddon;
import com.offz.spigot.mobzy.MobzyKt;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AbyssalCreatures extends JavaPlugin implements MobzyAddon {
    @Override
    public void onEnable() {
        getLogger().info("On enable has been called");
        saveDefaultConfig();
        registerWithMobzy(MobzyKt.getMobzy());
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Plugin shutdown logic
        getLogger().info("onDisable has been invoked!");
    }

    @Override
    public void registerWithMobzy(Mobzy mobzy) {
        MobzyAPIKt.registerMobConfig(new File(getDataFolder(), "mobs.yml"), this);
        new AbyssalType();
        AbyssalType.registerTypes();

        MobzyAPIKt.registerSpawnConfig(new File(getDataFolder(), "spawns.yml"), this);
        mobzy.reloadExistingEntities();
    }
}