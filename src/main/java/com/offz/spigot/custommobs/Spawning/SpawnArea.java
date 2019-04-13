package com.offz.spigot.custommobs.Spawning;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;

public class SpawnArea {
    private Location location;
    private long time;
    private int lightLevel;
    private String areaType;

    public SpawnArea(Chunk chunk) {
        World world = chunk.getWorld();

        if(chunk.getChunkSnapshot().isSectionEmpty(255)){
            areaType = "empty";
        }


        this.time = world.getTime();
    }

    public String determineAreaType(List<Location> locations) {
        double air = 0, plains = 0, cliff = 0, water = 0;
        for (Location l : locations) {
            Material type = l.getBlock().getType();
            if (type.equals(Material.AIR))
                air++;
            else if (type.equals(Material.GRASS_BLOCK))
                plains++;
            else if (type.equals(Material.STONE))
                cliff++;
            else if (type.equals(Material.WATER))
                water++;

        }
        return "plains";
    }
}
