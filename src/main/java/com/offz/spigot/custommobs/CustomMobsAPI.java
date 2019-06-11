package com.offz.spigot.custommobs;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Loading.CustomType;
import com.offz.spigot.custommobs.Loading.SpawnRegistry;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import com.offz.spigot.custommobs.Spawning.MobSpawn;
import net.minecraft.server.v1_13_R2.Entity;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;

import java.util.List;
import java.util.Map;

public class CustomMobsAPI {
    private static boolean debug;

    public static void loadConfigValues(CustomMobs plugin) {
        FileConfiguration config = plugin.getConfig();

        debug = config.getBoolean("debug");
    }

    public static void debug(String message) {
        if (debug)
            Bukkit.broadcastMessage(message);
    }

    public static void registerSpawn(String layerName, MobSpawn spawn) {
        Map<String, List<MobSpawn>> spawns = SpawnRegistry.getLayerSpawns();
        if (!spawns.containsKey(layerName))
            return;
        spawns.get(layerName).add(spawn);
    }

    public static void unregisterSpawn(String layerName, MobSpawn spawn) {
        Map<String, List<MobSpawn>> spawns = SpawnRegistry.getLayerSpawns();
        if (!spawns.containsKey(layerName))
            return;
        spawns.get(layerName).remove(spawn);
    }

    public static Entity toNMS(org.bukkit.entity.Entity e) {
        return ((CraftEntity) e).getHandle();
    }

    public static String getMobID(org.bukkit.entity.Entity e) {
        return CustomType.toEntityTypeID(getBuilder(e).getName());
    }

    public static String getMobID(Entity e) {
        return CustomType.toEntityTypeID(getBuilder(e).getName());
    }

    public static boolean isMobOfType(org.bukkit.entity.Entity e, String name) {
        return isMobOfType(toNMS(e), name);
    }

    public static boolean isMobOfType(Entity e, String name) {
        return getMobID(e).equals(name);
    }

    public static boolean isCustomMob(org.bukkit.entity.Entity e) {
        return isCustomMob(toNMS(e));
    }

    public static boolean isCustomMob(Entity e) {
        return e instanceof CustomMob;
    }

    public static MobBuilder getBuilder(org.bukkit.entity.Entity e) {
        return getBuilder(((CraftEntity) e).getHandle());
    }

    public static MobBuilder getBuilder(Entity e) {
        if (e instanceof CustomMob)
            return ((CustomMob) e).getBuilder();
        return null;
    }
}
