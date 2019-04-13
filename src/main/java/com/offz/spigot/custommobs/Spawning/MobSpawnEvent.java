package com.offz.spigot.custommobs.Spawning;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public class MobSpawnEvent {
    private List<MobSpawn> mobSpawns = new ArrayList<>();
    private List<String> validLayers = new ArrayList<>();
    private List<Material> blockWhitelist = new ArrayList<>();
    private Map<String, Integer> preferredAreas = new HashMap<>();

    private long startTime;
    private long endTime;
    private double basePriority;
    private long minLightLevel;
    private long maxLightLevel;
    private int entityCap;

    public MobSpawnEvent(MobSpawn is) {
        mobSpawns.add(is);
    }

    public void spawnMobs(Location p) {
        for (MobSpawn s : mobSpawns) {
            s.spawn(p);
        }
    }

    public double getPriority(int time, int lightLevel, String areaType){
        double priority = basePriority;

        if(time < startTime || time > endTime)
            return(0);
        if(lightLevel < minLightLevel || lightLevel > maxLightLevel)
            return(0);

        priority += preferredAreas.get(areaType);
        return priority;
//        this.basePriority +
    }
}
