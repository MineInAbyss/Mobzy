package com.offz.spigot.custommobs.Spawning;

import com.offz.spigot.custommobs.Spawning.Vertical.SpawnArea;

//TODO I don't know if it makes a lot of sense to call this an event, or to convert it into a proper
// spigot event, but can't really think of something better to call it.

/**
 * A wrapper for a mob spawn that'll be done later, with a predetermined spawn number, so they can be decided
 * asynchronously then spawned in synchronously afterwards, since you can't directly interact with Bukkit
 * asynchronously.
 */
public class MobSpawnEvent {
    private MobSpawn mobSpawn;
    private SpawnArea area;
    private int spawns;

    public MobSpawnEvent(MobSpawn mobSpawn, SpawnArea location) {
        this.mobSpawn = mobSpawn;
        this.area = location;
        this.spawns = mobSpawn.chooseSpawnAmount();
    }

    public int getSpawns() {
        return spawns;
    }

    public void spawn() {
        mobSpawn.spawn(area, spawns);
    }
}
