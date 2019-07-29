package com.offz.spigot.mobzy;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * Stores context for the plugin, such as the plugin instance
 */
public class MobzyContext {
    private Plugin plugin;
    private Logger logger;
    private Configuration config;

    private MobzyConfig mobzyConfig;

    public MobzyContext(Configuration config, MobzyConfig mobzyConfig) {
        this.config = config;
        this.mobzyConfig = mobzyConfig;
    }

    public MobzyConfig getMobzyConfig() {
        return mobzyConfig;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Configuration getConfig() {
        return config;
    }
}
