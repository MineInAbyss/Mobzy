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

    private ConfigManager configManager;

    public MobzyContext(Configuration config, ConfigManager configManager) {
        this.config = config;
        this.configManager = configManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
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
