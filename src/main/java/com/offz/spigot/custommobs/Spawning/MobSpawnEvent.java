package com.offz.spigot.custommobs.Spawning;

import org.bukkit.Location;

public class MobSpawnEvent {
    private MobSpawn mobSpawn;
    private Location location;
    private int spawns;

    public MobSpawnEvent(MobSpawn mobSpawn, Location location) {
        this.mobSpawn = mobSpawn;
        this.location = location;
        this.spawns = mobSpawn.chooseSpawnAmount();
    }

    public MobSpawnEvent(MobSpawn mobSpawn, SpawnArea area) {
        this(mobSpawn, area.getSpawnLocation(mobSpawn.getSpawnPos()));
    }

    public int getSpawns() {
        return spawns;
    }

    public void spawn() {
        mobSpawn.spawn(location, spawns);
    }

}
