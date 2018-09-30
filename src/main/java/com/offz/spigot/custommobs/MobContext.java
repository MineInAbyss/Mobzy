package com.offz.spigot.custommobs;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;
import java.util.logging.Logger;

/**
 * Stores context for the plugin, such as the plugin instance
 */
public class MobContext {
    private Plugin plugin;
    private Logger logger;
    private Configuration config;

    public MobContext(Configuration config) {
        this.config = config;
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
