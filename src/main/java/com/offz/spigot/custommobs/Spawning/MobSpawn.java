package com.offz.spigot.custommobs.Spawning;

import com.offz.spigot.custommobs.Loading.CustomType;
import org.bukkit.Location;

import java.util.Random;

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

    private MobSpawn() {
    }

    public String getMobID() {
        return mobID;
    }

    public boolean spawn(Location p) {
        int spawned = 0;
        int amount = getSpawnAmount();
        if (radius == 0) {
            CustomType.spawnEntity(mobID, p);
            return true;
        }

        for (int i = 0; i < amount; i++) {
            Location l = SpawnTask.getSpawnLocation(p, 0, radius);
            if (l == null)
                CustomType.spawnEntity(mobID, p);
            else {
                CustomType.spawnEntity(mobID, l);
                spawned += 1;
            }
        }
//        Bukkit.broadcastMessage(spawned + " spawned");
        return spawned > 0;
    }

    public double getPriority(Location l) {
        double priority = basePriority;
        long time = l.getWorld().getTime();
        int lightLevel = l.getBlock().getLightLevel();

        //eliminate impossible spawns
        if (time < minTime || time > maxTime)
            return (0);
        if (lightLevel < minLightLevel || lightLevel > maxLightLevel)
            return (0);

        if (l.getBlockY() < minY || l.getBlockY() > maxY)
            return (0);


        return priority;
    }

    public int getSpawnAmount() {
        if (minAmount == maxAmount)
            return minAmount;
        return new Random().nextInt((maxAmount - minAmount) + 1) + minAmount;
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

        public MobSpawn build() {
            MobSpawn mobSpawn = new MobSpawn();
            mobSpawn.basePriority = this.basePriority;
            mobSpawn.maxLightLevel = this.maxLightLevel;
            mobSpawn.mobID = this.mobID;
            mobSpawn.minAmount = this.minAmount;
            mobSpawn.radius = this.radius;
            mobSpawn.minY = this.minY;
            mobSpawn.maxTime = this.maxTime;
            mobSpawn.minLightLevel = this.minLightLevel;
            mobSpawn.maxY = this.maxY;
            mobSpawn.minTime = this.minTime;
            mobSpawn.maxAmount = this.maxAmount;
            return mobSpawn;
        }
    }
}
