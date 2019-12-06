package com.offz.spigot.mobzy;

import com.offz.spigot.mobzy.Builders.MobBuilder;
import com.offz.spigot.mobzy.Mobs.CustomMob;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MobzyAPI {
    private static Mobzy plugin;

    public MobzyAPI(Mobzy plugin) {
        MobzyAPI.plugin = plugin;
    }

    /**
     * Broadcast a message if the debug option is enabled in config
     *
     * @param message the message to be sent
     */
    public static void debug(String message) {
        if (plugin.getMobzyConfig().isDebug())
            Bukkit.broadcastMessage(message);
    }

    /**
     * @param mob the given entity
     * @return whether it is a renamed mob registered with Mobzy
     */
    public static boolean isRenamed(org.bukkit.entity.Entity mob) {
        Entity nmsMob = toNMS(mob);
        if (!isCustomMob(nmsMob) || mob.getCustomName() == null)
            return false;

        return !(mob.getCustomName().equals(((CustomMob) nmsMob).getBuilder().getName()));
    }

    /**
     * Registers a separate plugin's the spawn configuration file with the API
     *
     * @param configuration the file in which to look for a configuration
     * @param plugin        the plugin this configuration file corresponds to
     */
    public static void registerSpawnConfig(File configuration, JavaPlugin plugin) {
        MobzyAPI.plugin.getMobzyConfig().registerSpawnCfg(configuration, plugin);
    }

    /**
     * Registers a separate plugin's the mob configuration file with the API (to read mob attributes)
     *
     * @param configuration the file in which to look for a configuration
     * @param plugin        the plugin this configuration file corresponds to
     */
    public static void registerMobConfig(File configuration, JavaPlugin plugin) {
        MobzyAPI.plugin.getMobzyConfig().registerMobCfg(configuration, plugin);
    }

    /**
     * Converts a Bukkit entity to an NMS entity
     *
     * @param e the Bukkit entity
     * @return the converted NMS entity
     */
    public static Entity toNMS(org.bukkit.entity.Entity e) {
        return ((CraftEntity) e).getHandle();
    }

    /**
     * Returns a mob ID for a given entity
     *
     * @param e the entity
     * @return its mob ID
     */
    public static String getMobID(org.bukkit.entity.Entity e) {
        return CustomType.toEntityTypeID(getBuilder(e).getName());
    }

    /**
     * Returns a mob ID for a given entity
     *
     * @param e the entity
     * @return its mob ID
     */
    public static String getMobID(Entity e) {
        return CustomType.toEntityTypeID(getBuilder(e).getName());
    }

    /**
     * @param e     the entity to be checked
     * @param mobID the mob ID to compare to
     * @return whether the mob is of type of the given mob ID
     */
    public static boolean isMobOfType(org.bukkit.entity.Entity e, String mobID) {
        return isMobOfType(toNMS(e), mobID);
    }

    /**
     * @param e     the entity to be checked
     * @param mobID the mob ID to compare to
     * @return whether the mob is of type of the given mob ID
     */
    public static boolean isMobOfType(Entity e, String mobID) {
        return getMobID(e).equals(mobID);
    }

    /**
     * @param e the entity to be checked
     * @return whether it is a custom mob registered with Mobzy
     */
    public static boolean isCustomMob(org.bukkit.entity.Entity e) {
        return isCustomMob(toNMS(e));
    }

    /**
     * @param e the entity to be checked
     * @return whether it is a custom mob registered with Mobzy
     */
    public static boolean isCustomMob(Entity e) {
        return e instanceof CustomMob;
    }

    /**
     * @param e the given entity
     * @return its {@link MobBuilder} or null if the entity is not registered with Mobzy
     */
    public static MobBuilder getBuilder(org.bukkit.entity.Entity e) {
        return getBuilder(((CraftEntity) e).getHandle());
    }

    /**
     * @param e the given entity
     * @return its {@link MobBuilder} or null if the entity is not registered with Mobzy
     */
    public static MobBuilder getBuilder(Entity e) {
        if (isCustomMob(e))
            return ((CustomMob) e).getBuilder();
        return null;
    }

    /**
     * @param name the name of an entity type
     * @return its builder
     */
    public static MobBuilder getBuilder(String name) {
        return CustomType.getBuilder(name);
    }

    public static EntityTypes<?> getEntityType(Entity entity) {
        return entity.P();
    }

    public MobzyAPI getInstance() {
        return plugin.getApi();
    }
}
