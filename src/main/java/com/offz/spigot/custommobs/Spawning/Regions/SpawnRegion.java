package com.offz.spigot.custommobs.Spawning.Regions;

import com.offz.spigot.custommobs.Mobs.Flying.FlyingMob;
import com.offz.spigot.custommobs.Mobs.Hostile.HostileMob;
import com.offz.spigot.custommobs.Mobs.Passive.PassiveMob;
import com.offz.spigot.custommobs.Spawning.MobSpawn;

import java.util.ArrayList;
import java.util.List;

/**
 * A region with determined hostile, passive, flying, etc... spawns. Currently only layers are treated as regions.
 * In the future, this will integrate with WorldGuard regions to allow for more specific region setting
 */
public class SpawnRegion {
    //TODO maybe mob caps should be determined per region?
    private List<MobSpawn> passiveSpawns = new ArrayList<>();
    private List<MobSpawn> hostileSpawns = new ArrayList<>();
    private List<MobSpawn> flyingSpawns = new ArrayList<>();
    private String name;

    public SpawnRegion(String name, MobSpawn... spawns) {
        this.name = name;

        for (MobSpawn spawn : spawns) {
            addSpawn(spawn);
        }
    }

    public List<MobSpawn> getPassiveSpawns() {
        return passiveSpawns;
    }

    public List<MobSpawn> getHostileSpawns() {
        return hostileSpawns;
    }

    public List<MobSpawn> getFlyingSpawns() {
        return flyingSpawns;
    }

    public List<MobSpawn> getSpawnsFor(int mobType) {
        switch (mobType) {
            case 0:
                return getPassiveSpawns();
            case 1:
                return getHostileSpawns();
            case 2:
                return getFlyingSpawns();
        }
        return null;
    }

    public void addSpawn(MobSpawn spawn) {
        //add to a different spawn list depending on what kind of entity type it is (since we have separate mob caps per list)
        Class entityClass = spawn.getEntityType().c();
        if (PassiveMob.class.isAssignableFrom(entityClass))
            passiveSpawns.add(spawn);
        else if (HostileMob.class.isAssignableFrom(entityClass))
            hostileSpawns.add(spawn);
        else if (FlyingMob.class.isAssignableFrom(entityClass))
            flyingSpawns.add(spawn);
    }
}
