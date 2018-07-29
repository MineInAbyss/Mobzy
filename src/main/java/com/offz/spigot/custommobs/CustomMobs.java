package com.offz.spigot.custommobs;

import org.bukkit.plugin.java.JavaPlugin;

public final class CustomMobs extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("On enable has been called");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("onDisable has been invoked!");
    }
}
