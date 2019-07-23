package com.offz.spigot.custommobs.Spawning;

import com.offz.spigot.custommobs.CustomMobsAPI;
import com.offz.spigot.custommobs.CustomType;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import com.offz.spigot.custommobs.Spawning.Vertical.SpawnArea;
import com.offz.spigot.custommobs.Spawning.Vertical.VerticalSpawn;
import net.minecraft.server.v1_13_R2.EntityTypes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MobSpawn {
    private EntityTypes entityType;
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
    private List<String> sections = new ArrayList<>();

    private MobSpawn() {
    }

    public static boolean enoughSpace(Location loc, double width, double height) {
        double checkRad = width / 2;

        for (int y = 0; y < Math.ceil(height); y++) {
            for (double x = -checkRad; x < checkRad; x++) {
                for (double z = -checkRad; z < checkRad; z++) {
                    Block checkBlock = loc.clone().add(x, y, z).getBlock();
                    if (checkBlock.getType().isOccluding()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public SpawnPosition getSpawnPos() {
        return spawnPos;
    }

    public int spawn(SpawnArea area) {
        return spawn(area, chooseSpawnAmount());
    }

    public EntityTypes getEntityType() {
        return entityType;
    }

    public static Location getSpawnInRadius(Location loc, double minRad, double maxRad) {
        for (int i = 0; i < 30; i++) {
            double y = (Math.random() - 0.5) * maxRad;
            if (Math.abs(y) > minRad) //if y is minRad blocks away from player, mobs can spawn directly under or above
                minRad = 0;

            double x = Math.signum(Math.random() - 0.5) * ((Math.random() * (maxRad - minRad)) + minRad);
            double z = Math.signum(Math.random() - 0.5) * ((Math.random() * (maxRad - minRad)) + minRad);

            if (!loc.getChunk().isLoaded())
                return null;
            Location searchLoc = loc.clone();
            searchLoc = searchLoc.add(new Vector(x, y, z));

            if (!searchLoc.getBlock().getType().isSolid()) {
                searchLoc = VerticalSpawn.checkDown(searchLoc, 25);
                if (searchLoc != null)
                    return searchLoc;
            } else {
                searchLoc = VerticalSpawn.checkUp(searchLoc, 25);
                if (searchLoc != null)
                    return searchLoc;
            }
        }
        return null;
    }

    public int spawn(SpawnArea area, int spawns) {
        Location loc = area.getSpawnLocation(getSpawnPos());
        for (int i = 0; i < spawns; i++) {
            Entity entity;
            if (radius == 0) {
                entity = CustomType.spawnEntity(entityType, loc);
            } else {

                Location spawnLoc;

                if (radius != 0 && !spawnPos.equals(SpawnPosition.AIR) && (spawnLoc = getSpawnInRadius(loc, 0, radius)) != null)
                    entity = CustomType.spawnEntity(entityType, spawnLoc);
                else
                    entity = CustomType.spawnEntity(entityType, loc);
            }
            net.minecraft.server.v1_13_R2.Entity nmsEntity = CustomMobsAPI.toNMS(entity);
            //TODO could be a better way of handling mobs spawning with too little space (in getPriority) but this works well enough for now
            if (!enoughSpace(loc, nmsEntity.width, nmsEntity.length)) { //length is actually the height, don't know why, it's just how it be
                CustomMobsAPI.debug(ChatColor.YELLOW + "Removed " + ((CustomMob) nmsEntity).getBuilder().getName() + " because of lack of space");
                nmsEntity.die();
            }
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
        if (!whitelist.isEmpty() && !whitelist.contains(l.clone().add(0, -1, 0).getBlock().getType()))
            return -1;
        //TODO remove
//        if (!sections.isEmpty() && !sections.contains(MineInAbyss.getContext().getRealWorldManager().getSectionFor(l).toString()))
//            return -1;

        return priority;
    }

    public int chooseSpawnAmount() {
        if (minAmount >= maxAmount)
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
        if (entityType != null ? !entityType.equals(mobSpawn.entityType) : mobSpawn.entityType != null) return false;
        return spawnPos == mobSpawn.spawnPos;
    }

    public enum SpawnPosition {
        AIR,
        GROUND,
        OVERHANG
    }

    public static final class Builder {
        private EntityTypes entityType;
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
        private List<String> sections = new ArrayList<>();

        public Builder() {
        }

        public Builder(MobSpawn old) {
            setEntityType(old.entityType).setMinAmount(old.minAmount).setMaxAmount(old.maxAmount).setRadius(old.radius).setBasePriority(old.basePriority).setMinTime(old.minTime).setMaxTime(old.maxTime).setMinLightLevel(old.minLightLevel).setMaxLightLevel(old.maxLightLevel).setMinY(old.minY).setMaxY(old.maxY).setMinGap(old.minGap).setMaxGap(old.maxGap).setSpawnPos(old.spawnPos).setWhitelist(old.whitelist).setSections(old.sections);
        }

        public static Builder aMobSpawn() {
            return new Builder();
        }

        public Builder setEntityType(EntityTypes entityType) {
            this.entityType = entityType;
            return this;
        }

        public Builder setMinAmount(int minAmount) {
            this.minAmount = minAmount;
            return this;
        }

        public Builder setMaxAmount(int maxAmount) {
            this.maxAmount = maxAmount;
            return this;
        }

        public Builder setRadius(double radius) {
            this.radius = radius;
            return this;
        }

        public Builder setBasePriority(double basePriority) {
            this.basePriority = basePriority;
            return this;
        }

        public Builder setMinTime(long minTime) {
            this.minTime = minTime;
            return this;
        }

        public Builder setMaxTime(long maxTime) {
            this.maxTime = maxTime;
            return this;
        }

        public Builder setMinLightLevel(long minLightLevel) {
            this.minLightLevel = minLightLevel;
            return this;
        }

        public Builder setMaxLightLevel(long maxLightLevel) {
            this.maxLightLevel = maxLightLevel;
            return this;
        }

        public Builder setMinY(int minY) {
            this.minY = minY;
            return this;
        }

        public Builder setMaxY(int maxY) {
            this.maxY = maxY;
            return this;
        }

        public Builder setMinGap(int minGap) {
            this.minGap = minGap;
            return this;
        }

        public Builder setMaxGap(int maxGap) {
            this.maxGap = maxGap;
            return this;
        }

        public Builder setSpawnPos(SpawnPosition spawnPos) {
            this.spawnPos = spawnPos;
            return this;
        }

        public Builder setWhitelist(List<Material> whitelist) {
            this.whitelist = whitelist;
            return this;
        }

        public Builder setSections(List<String> sections) {
            this.sections = sections;
            return this;
        }

        public MobSpawn build() {
            MobSpawn mobSpawn = new MobSpawn();
            mobSpawn.maxTime = this.maxTime;
            mobSpawn.sections = this.sections;
            mobSpawn.radius = this.radius;
            mobSpawn.whitelist = this.whitelist;
            mobSpawn.minAmount = this.minAmount;
            mobSpawn.spawnPos = this.spawnPos;
            mobSpawn.maxLightLevel = this.maxLightLevel;
            mobSpawn.minLightLevel = this.minLightLevel;
            mobSpawn.maxAmount = this.maxAmount;
            mobSpawn.basePriority = this.basePriority;
            mobSpawn.entityType = this.entityType;
            mobSpawn.maxY = this.maxY;
            mobSpawn.minY = this.minY;
            mobSpawn.maxGap = this.maxGap;
            mobSpawn.minTime = this.minTime;
            mobSpawn.minGap = this.minGap;
            return mobSpawn;
        }
    }
}
