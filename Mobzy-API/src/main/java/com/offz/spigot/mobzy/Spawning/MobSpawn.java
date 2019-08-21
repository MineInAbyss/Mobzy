package com.offz.spigot.mobzy.Spawning;

import com.offz.spigot.mobzy.CustomType;
import com.offz.spigot.mobzy.Mobs.CustomMob;
import com.offz.spigot.mobzy.MobzyAPI;
import com.offz.spigot.mobzy.Spawning.Vertical.SpawnArea;
import com.offz.spigot.mobzy.Spawning.Vertical.VerticalSpawn;
import net.minecraft.server.v1_13_R2.EntityTypes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MobSpawn implements ConfigurationSerializable {
    private EntityTypes entityType;
    private int minAmount = 1;
    private int maxAmount = 1;
    private double radius = 0;
    private double basePriority = 1;
    private long minTime = -1;
    private long maxTime = 10000000;
    private long minLight = 0;
    private long maxLight = 100;
    private int minY = 0;
    private int maxY = 256;
    private int minGap = 0;
    private int maxGap = 256;
    private int maxLocalGroup = -1;
    private double localGroupRadius = 10;
    private SpawnPosition spawnPos = SpawnPosition.GROUND;
    private List<Material> whitelist = new ArrayList<>();

    private MobSpawn() {
    }

    private MobSpawn(Builder builder) {
        entityType = builder.entityType;
        minAmount = builder.minAmount;
        maxAmount = builder.maxAmount;
        radius = builder.radius;
        basePriority = builder.basePriority;
        minTime = builder.minTime;
        maxTime = builder.maxTime;
        minLight = builder.minLight;
        maxLight = builder.maxLight;
        minY = builder.minY;
        maxY = builder.maxY;
        minGap = builder.minGap;
        maxGap = builder.maxGap;
        maxLocalGroup = builder.maxLocalGroup;
        localGroupRadius = builder.localGroupRadius;
        spawnPos = builder.spawnPos;
        whitelist = builder.whitelist;
    }

    /**
     * Checks if there is enough space to spawn an entity in a given location without it suffocating
     * TODO currently gives many false positives
     *
     * @param loc    the location to check
     * @param width  the width of the entity
     * @param height the height of the entity
     * @return whether it will spawn without suffocating
     */
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

    /**
     * Gets a location to spawn in a mob given an original location and min/max radii around it
     *
     * @param loc    the location to check off of
     * @param minRad the minimum radius for the new location to be picked at
     * @param maxRad the maximum radius for the new location to be picked at
     * @return a new position to spawn in
     */
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

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(MobSpawn copy) {
        Builder builder = new Builder();
        builder.entityType = copy.getEntityType();
        builder.minAmount = copy.getMinAmount();
        builder.maxAmount = copy.getMaxAmount();
        builder.radius = copy.getRadius();
        builder.basePriority = copy.getBasePriority();
        builder.minTime = copy.getMinTime();
        builder.maxTime = copy.getMaxTime();
        builder.minLight = copy.getMinLight();
        builder.maxLight = copy.getMaxLight();
        builder.minY = copy.getMinY();
        builder.maxY = copy.getMaxY();
        builder.minGap = copy.getMinGap();
        builder.maxGap = copy.getMaxGap();
        builder.maxLocalGroup = copy.getMaxLocalGroup();
        builder.localGroupRadius = copy.getLocalGroupRadius();
        builder.spawnPos = copy.getSpawnPos();
        builder.whitelist = copy.getWhitelist();
        return builder;
    }

    public static MobSpawn deserialize(Map<String, Object> args) {
        return deserialize(args, newBuilder());
    }

    public static MobSpawn deserialize(Map<String, Object> args, Builder applyTo) {
        if (args.containsKey("mob"))
            applyTo.setEntityType(CustomType.getType((String) args.get("mob")));
        if (args.containsKey("priority"))
            applyTo.setBasePriority((Double) args.get("priority"));
        if (args.containsKey("min-amount"))
            applyTo.setMinAmount((Integer) args.get("min-amount"));
        if (args.containsKey("max-amount"))
            applyTo.setMaxAmount((Integer) args.get("max-amount"));
        if (args.containsKey("min-gap"))
            applyTo.setMinGap((Integer) args.get("min-gap"));
        if (args.containsKey("max-gap"))
            applyTo.setMaxGap((Integer) args.get("max-gap"));
        if (args.containsKey("min-light"))
            applyTo.setMinLight((Integer) args.get("min-light"));
        if (args.containsKey("max-light"))
            applyTo.setMaxLight((Integer) args.get("max-light"));
        if (args.containsKey("min-time"))
            applyTo.setMinTime((Long) args.get("min-time"));
        if (args.containsKey("max-time"))
            applyTo.setMaxTime((Long) args.get("max-time"));
        if (args.containsKey("min-y"))
            applyTo.setMinY((Integer) args.get("min-y"));
        if (args.containsKey("max-y"))
            applyTo.setMaxY((Integer) args.get("max-y"));
        if (args.containsKey("max-local-group"))
            applyTo.setMaxLocalGroup((Integer) args.get("max-local-group"));
        if (args.containsKey("local-group-radius"))
            applyTo.setLocalGroupRadius(((Number) args.get("local-group-radius")).doubleValue());
        if (args.containsKey("radius"))
            applyTo.setRadius((Integer) args.get("radius"));
        if (args.containsKey("spawn-pos")) {
            SpawnPosition spawnPos = SpawnPosition.GROUND;
            switch ((String) args.get("spawn-pos")) {
                case "AIR":
                    spawnPos = SpawnPosition.AIR;
                    break;
                case "GROUND":
                    spawnPos = SpawnPosition.GROUND;
                    break;
                case "OVERHANG":
                    spawnPos = SpawnPosition.OVERHANG;
                    break;
            }
            applyTo.setSpawnPos(spawnPos);
        }
        if (args.containsKey("block-whitelist")) {
            List<Material> materialWhiteist = ((List<String>) args.get("block-whitelist")).stream()
                    .map(Material::valueOf)
                    .collect(Collectors.toList());

            applyTo.setWhitelist(materialWhiteist);
        }

        return applyTo.build();
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public double getRadius() {
        return radius;
    }

    public double getBasePriority() {
        return basePriority;
    }

    public long getMinTime() {
        return minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public long getMinLight() {
        return minLight;
    }

    public long getMaxLight() {
        return maxLight;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinGap() {
        return minGap;
    }

    public int getMaxGap() {
        return maxGap;
    }


    public int getMaxLocalGroup() {
        return maxLocalGroup;
    }

    public double getLocalGroupRadius() {
        return localGroupRadius;
    }

    public List<Material> getWhitelist() {
        return whitelist;
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
            net.minecraft.server.v1_13_R2.Entity nmsEntity = MobzyAPI.toNMS(entity);
            //TODO could be a better way of handling mobs spawning with too little space (in getPriority) but this works well enough for now
            if (!enoughSpace(loc, nmsEntity.width, nmsEntity.length)) { //length is actually the height, don't know why, it's just how it be
                MobzyAPI.debug(ChatColor.YELLOW + "Removed " + ((CustomMob) nmsEntity).getBuilder().getName() + " because of lack of space");
                nmsEntity.die();
            }
        }
        return spawns;
    }

    public double getPriority(SpawnArea spawnArea, List<MobSpawnEvent> toSpawn) {
        if (spawnArea.getGap() < minGap || spawnArea.getGap() > maxGap)
            return -1;

        return getPriority(spawnArea.getSpawnLocation(spawnPos), toSpawn);
    }

    public double getPriority(Location l, List<MobSpawnEvent> toSpawn) {
        double priority = basePriority;
        long time = l.getWorld().getTime();
        int lightLevel = l.getBlock().getLightLevel();

        //eliminate impossible spawns
        if (time < minTime || time > maxTime)
            return -1;
        if (lightLevel < minLight || lightLevel > maxLight)
            return -1;
        if (l.getBlockY() < minY || l.getBlockY() > maxY)
            return -1;
        if (!whitelist.isEmpty() && !whitelist.contains(l.clone().add(0, -1, 0).getBlock().getType()))
            return -1;
        //if too many entities of the same type nearby
        if (maxLocalGroup > 0) {
            int nearbyEntities = 0;
            for (Entity e : l.getWorld().getNearbyEntities(l, localGroupRadius, localGroupRadius, localGroupRadius)) { //TODO this doesnt factor in planned-to-spawn entities
                if (MobzyAPI.isCustomMob(e) && ((CustomMob) MobzyAPI.toNMS(e)).getEntityType().equals(entityType))
                    nearbyEntities++;
                if (nearbyEntities >= maxLocalGroup) return -1;
            }

            for (MobSpawnEvent spawn : toSpawn) {
                if(spawn.getLocation().getWorld().equals(l.getWorld()) && spawn.getLocation().distance(l) < localGroupRadius)
                    nearbyEntities++;
                if (nearbyEntities >= maxLocalGroup) return -1;
            }

        }
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
        if (minLight != mobSpawn.minLight) return false;
        if (maxLight != mobSpawn.maxLight) return false;
        if (minY != mobSpawn.minY) return false;
        if (maxY != mobSpawn.maxY) return false;
        if (minGap != mobSpawn.minGap) return false;
        if (maxGap != mobSpawn.maxGap) return false;
        if (entityType != null ? !entityType.equals(mobSpawn.entityType) : mobSpawn.entityType != null) return false;
        return spawnPos == mobSpawn.spawnPos;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
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
        private long minLight = 0;
        private long maxLight = 100;
        private int minY = 0;
        private int maxY = 256;
        private int minGap = 0;
        private int maxGap = 256;
        private int maxLocalGroup = -1;
        private double localGroupRadius = 10;
        private SpawnPosition spawnPos = SpawnPosition.GROUND;
        private List<Material> whitelist = new ArrayList<>();

        private Builder() {
        }

        public Builder setEntityType(EntityTypes val) {
            entityType = val;
            return this;
        }

        public Builder setMinAmount(int val) {
            minAmount = val;
            return this;
        }

        public Builder setMaxAmount(int val) {
            maxAmount = val;
            return this;
        }

        public Builder setRadius(double val) {
            radius = val;
            return this;
        }

        public Builder setBasePriority(double val) {
            basePriority = val;
            return this;
        }

        public Builder setMinTime(long val) {
            minTime = val;
            return this;
        }

        public Builder setMaxTime(long val) {
            maxTime = val;
            return this;
        }

        public Builder setMinLight(long val) {
            minLight = val;
            return this;
        }

        public Builder setMaxLight(long val) {
            maxLight = val;
            return this;
        }

        public Builder setMinY(int val) {
            minY = val;
            return this;
        }

        public Builder setMaxY(int val) {
            maxY = val;
            return this;
        }

        public Builder setMinGap(int val) {
            minGap = val;
            return this;
        }

        public Builder setMaxGap(int val) {
            maxGap = val;
            return this;
        }

        public Builder setMaxLocalGroup(int val) {
            maxLocalGroup = val;
            return this;
        }

        public Builder setLocalGroupRadius(double val) {
            localGroupRadius = val;
            return this;
        }

        public Builder setSpawnPos(SpawnPosition val) {
            spawnPos = val;
            return this;
        }

        public Builder setWhitelist(List<Material> val) {
            whitelist = val;
            return this;
        }

        public MobSpawn build() {
            return new MobSpawn(this);
        }
    }
}
