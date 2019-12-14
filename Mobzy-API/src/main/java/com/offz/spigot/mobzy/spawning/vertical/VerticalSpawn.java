package com.offz.spigot.mobzy.spawning.vertical;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class VerticalSpawn {
    private List<SpawnArea> spawnAreas;
    private Location originalLoc;
    private int minY;
    private int maxY;
    public VerticalSpawn(Location originalLoc, int minY, int maxY) {
        this.originalLoc = originalLoc;
        this.minY = minY;
        this.maxY = maxY;

        spawnAreas = findBlockPairs(originalLoc);
    }

    private List<SpawnArea> findBlockPairs(Location l) {
        //boundaries
        if (maxY > 256)
            maxY = 256;
        if (minY < 0)
            minY = 0;

        List<SpawnArea> locations = new ArrayList<>();
        Location highest = getHighestBlock(l, minY, maxY);
        if (highest.getBlockY() < maxY) { //if there's a gap with the sky, add the highest block and current
            if (highest.getBlockY() == minY) //if we found void, return an empty list
                return locations;
            //add the gap between the top and highest block (i.e. open sky)
            locations.add(new SpawnArea(new Location(highest.getWorld(), highest.getX(), 256, highest.getZ()), highest.clone().add(0, 1, 0)));
        } else //if there was no gap, there won't be any!
            return locations;

        //search for gaps and add them to the list as we go down
        Location searchL = highest.clone().add(0, -1, 0); //location for searching downwards
        Location top = searchL; //the top block of a section
        while (searchL.getBlockY() > minY) {
            Block prevBlock = searchL.getBlock();
            searchL = searchL.add(0, -1, 0);
            Block nextBlock = searchL.getBlock();

            if (!prevBlock.isPassable() && nextBlock.isPassable()) //if went from solid to air
                top = searchL.clone();
            else if (prevBlock.isPassable() && (!nextBlock.isPassable() || searchL.getBlockY() == 1)) //if went back to solid or reached the bottom of the world
                locations.add(new SpawnArea(top, searchL.clone().add(0, 1, 0)));
        }
        return locations;
    }

    public static Location checkDown(Location originalL, int maxI) {
        Location l = originalL.clone();
        for (int i = 0; i < maxI; i++) {
            l = l.add(0, -1, 0);

            if (l.getY() < 10)
                return null;
            if (l.getY() >= 256)
                l.setY(255);

            if (l.getBlock().getType().isSolid())
                return l.add(0, 1, 0);
        }
        return null;
    }

    public static Location checkUp(Location originalL, int maxI) {
        Location l = originalL.clone();
        for (int i = 0; i < maxI; i++) {
            l = l.add(0, 1, 0);

            if (!l.getBlock().getType().isSolid()) {
                return l;
            }

            if (l.getY() >= 256)
                return null;
            if (l.getY() < 10)
                l.setY(10);
        }
        return null;
    }

    public static Location getHighestBlock(Location l, int minY, int maxY) {
        //make a copy of the given location so we don't change its coords.
        Location highest = l.clone();
        highest.setY(maxY);

        while (highest.getBlock().isPassable() && !highest.getBlock().getType().equals(Material.WATER) && highest.getBlockY() > minY)
            highest.add(0, -1, 0);

        return highest;
    }

    public List<SpawnArea> getSpawnAreas() {
        return spawnAreas;
    }
}
