package com.offz.spigot.custommobs.Spawning;

import com.offz.spigot.custommobs.Spawning.Vertical.SpawnArea;

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
