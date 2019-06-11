package com.offz.spigot.custommobs.Spawning;

import com.offz.spigot.custommobs.Loading.CustomType;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MobSpawn {

    private String mobID;
    private int minAmount = 1;
    private int maxAmount = 1;
    private double radius = 0;
    private double basePriority = 1;
    private long minTime = -1;
    private long maxTime = 10000000;
    private long minLightLevel = 0;
    private long maxLightLevel = 100;
    private int minY = 0;
    private int maxY = 256;
    private int minGap = 0;
    private int maxGap = 256;
    private SpawnPosition spawnPos = SpawnPosition.GROUND;
    private List<Material> whitelist = new ArrayList<>();

    public SpawnPosition getSpawnPos() {
        return spawnPos;
    }

    private MobSpawn() {
    }

    public int spawn(Location loc) {
        return spawn(loc, chooseSpawnAmount());
    }

    public int spawn(Location loc, int spawns) {
        for (int i = 0; i < spawns; i++) {
            if (radius == 0) {
                CustomType.spawnEntity(mobID, loc);
                continue;
            }

            Location spawnLoc;
            if (radius != 0 && (spawnLoc = SpawnTask.getSpawnLocation(loc, 0, radius)) != null)
                CustomType.spawnEntity(mobID, spawnLoc);
            else
                CustomType.spawnEntity(mobID, loc);
        }
        return spawns;
    }

    public double getPriority(SpawnArea spawnArea) {
        if (spawnArea.getGap() < minGap || spawnArea.getGap() > maxGap)
            return -1;

        return getPriority(spawnArea.getSpawnLocation(spawnPos));
    }

    public double getPriority(Location l) {
        double priority = basePriority;
        long time = l.getWorld().getTime();
        int lightLevel = l.getBlock().getLightLevel();

        //eliminate impossible spawns
        if (time < minTime || time > maxTime)
            return -1;
        if (lightLevel < minLightLevel || lightLevel > maxLightLevel)
            return -1;
        if (l.getBlockY() < minY || l.getBlockY() > maxY)
            return -1;
        if(!whitelist.isEmpty() && !whitelist.contains(l.toBlockLocation().add(0, -1, 0).getBlock().getType()))
            return -1;

        return priority;
    }

    public int chooseSpawnAmount() {
        if (minAmount == maxAmount)
            return minAmount;
        return (int) (Math.random() * (maxAmount - minAmount + 1)) + minAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MobSpawn mobSpawn = (MobSpawn) o;

        if (minAmount != mobSpawn.minAmount) return false;
        if (maxAmount != mobSpawn.maxAmount) return false;
        if (Double.compare(mobSpawn.radius, radius) != 0) return false;
        if (Double.compare(mobSpawn.basePriority, basePriority) != 0) return false;
        if (minTime != mobSpawn.minTime) return false;
        if (maxTime != mobSpawn.maxTime) return false;
        if (minLightLevel != mobSpawn.minLightLevel) return false;
        if (maxLightLevel != mobSpawn.maxLightLevel) return false;
        if (minY != mobSpawn.minY) return false;
        if (maxY != mobSpawn.maxY) return false;
        if (minGap != mobSpawn.minGap) return false;
        if (maxGap != mobSpawn.maxGap) return false;
        if (mobID != null ? !mobID.equals(mobSpawn.mobID) : mobSpawn.mobID != null) return false;
        return spawnPos == mobSpawn.spawnPos;
    }

    public enum SpawnPosition {
        AIR,
        GROUND,
        OVERHANG
    }

    public static final class MobSpawnBuilder {
        private String mobID;
        private int minAmount = 1;
        private int maxAmount = 1;
        private double radius = 0;
        private double basePriority = 1;
        private long minTime = -1;
        private long maxTime = 10000000;
        private long minLightLevel = 0;
        private long maxLightLevel = 100;
        private int minY = 0;
        private int maxY = 256;
        private int minGap = 0;
        private int maxGap = 256;
        private SpawnPosition spawnPos = SpawnPosition.GROUND;
        private List<Material> whitelist = new ArrayList<>();

        public MobSpawnBuilder() {
        }

        public static MobSpawnBuilder aMobSpawn() {
            return new MobSpawnBuilder();
        }

        public MobSpawnBuilder withMobID(String mobID) {
            this.mobID = mobID;
            return this;
        }

        public MobSpawnBuilder withMinAmount(int minAmount) {
            this.minAmount = minAmount;
            return this;
        }

        public MobSpawnBuilder withMaxAmount(int maxAmount) {
            this.maxAmount = maxAmount;
            return this;
        }

        public MobSpawnBuilder withRadius(double radius) {
            this.radius = radius;
            return this;
        }

        public MobSpawnBuilder withBasePriority(double basePriority) {
            this.basePriority = basePriority;
            return this;
        }

        public MobSpawnBuilder withMinTime(long minTime) {
            this.minTime = minTime;
            return this;
        }

        public MobSpawnBuilder withMaxTime(long maxTime) {
            this.maxTime = maxTime;
            return this;
        }

        public MobSpawnBuilder withMinLightLevel(long minLightLevel) {
            this.minLightLevel = minLightLevel;
            return this;
        }

        public MobSpawnBuilder withMaxLightLevel(long maxLightLevel) {
            this.maxLightLevel = maxLightLevel;
            return this;
        }

        public MobSpawnBuilder withMinY(int minY) {
            this.minY = minY;
            return this;
        }

        public MobSpawnBuilder withMaxY(int maxY) {
            this.maxY = maxY;
            return this;
        }

        public MobSpawnBuilder withMinGap(int minGap) {
            this.minGap = minGap;
            return this;
        }

        public MobSpawnBuilder withMaxGap(int maxGap) {
            this.maxGap = maxGap;
            return this;
        }

        public MobSpawnBuilder withSpawnPos(SpawnPosition spawnPos) {
            this.spawnPos = spawnPos;
            return this;
        }

        public MobSpawnBuilder withWhitelist(List<Material> whitelist) {
            this.whitelist = whitelist;
            return this;
        }

        public MobSpawn build() {
            MobSpawn mobSpawn = new MobSpawn();
            mobSpawn.spawnPos = this.spawnPos;
            mobSpawn.whitelist = this.whitelist;
            mobSpawn.minLightLevel = this.minLightLevel;
            mobSpawn.minTime = this.minTime;
            mobSpawn.maxAmount = this.maxAmount;
            mobSpawn.maxTime = this.maxTime;
            mobSpawn.maxY = this.maxY;
            mobSpawn.minAmount = this.minAmount;
            mobSpawn.basePriority = this.basePriority;
            mobSpawn.mobID = this.mobID;
            mobSpawn.radius = this.radius;
            mobSpawn.minY = this.minY;
            mobSpawn.maxLightLevel = this.maxLightLevel;
            mobSpawn.maxGap = this.maxGap;
            mobSpawn.minGap = this.minGap;
            return mobSpawn;
        }
    }
}
