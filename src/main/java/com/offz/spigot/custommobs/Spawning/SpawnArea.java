package com.offz.spigot.custommobs.Spawning;

import org.bukkit.Location;

public class SpawnArea {
    private Location top;
    private Location bottom;
    private int gap;

    public SpawnArea(Location top, Location bottom) {
        this.top = top;
        this.bottom = bottom;
        gap = top.getBlockY() - bottom.getBlockY() + 1; //adding one since if the blocks are on the same block, they still have a gap of 1 from top to bottom
    }

    public Location getTop() {
        return top;
    }

    public Location getBottom() {
        return bottom;
    }

    public Location getSpawnLocation(MobSpawn.SpawnPosition spawnPosition){
        if(spawnPosition.equals(MobSpawn.SpawnPosition.GROUND))
            return getBottom();
        else
            return getTop();
    }

    public int getGap() {
        return gap;
    }

    @Override
    public String toString() {
        return "SpawnArea: " + bottom.getBlockY() + ", " + top.getBlockY();
    }
}
