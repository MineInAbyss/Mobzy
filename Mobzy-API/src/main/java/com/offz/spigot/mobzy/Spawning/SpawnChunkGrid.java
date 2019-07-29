package com.offz.spigot.mobzy.Spawning;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpawnChunkGrid {
    private List<ChunkSpawn> chunkSpawns = new ArrayList<>();

    SpawnChunkGrid(Location loc, int minRad, int maxRad) {
        Chunk chunk = loc.getChunk();
        int startX = chunk.getX();
        int startZ = chunk.getZ();
        World world = loc.getWorld();

        //add chunks in a circle around player to the list
        for (int x = -maxRad; x <= maxRad; x++) {
            for (int z = -maxRad; z <= maxRad; z++) {
                double dist = x * x + z * z;
                //if we are within the maximum circular radius and not within minimum, add the chunk to the list
                if (dist <= maxRad * maxRad) {
                    Chunk spawnChunk = world.getChunkAt(startX + x, startZ + z);
                    if (dist > minRad * minRad) {
                        ChunkSpawn chunkSpawn = new ChunkSpawn(spawnChunk, 0, 254);
                        addSpawnIfValid(chunkSpawn);
                    } else {
                        int minVertical = minRad * 16; //minimum vertical spawn distance is the number of chunks * width of a chunk
                        //do some checks to add areas above and below the player when we've reached inside the minimum radius
                        if (loc.getY() + minVertical < 254) {
                            ChunkSpawn topSpawn = new ChunkSpawn(spawnChunk, ((int) loc.getY() + minVertical), 254);
                            addSpawnIfValid(topSpawn);
                        }
                        if (loc.getY() - minVertical > 0) {
                            ChunkSpawn bottomSpawn = new ChunkSpawn(spawnChunk, 0, ((int) loc.getY() - minVertical));
                            addSpawnIfValid(bottomSpawn);
                        }
                    }
                }
            }
        }
        Collections.sort(chunkSpawns);
    }

    private void addSpawnIfValid(ChunkSpawn spawn) {
        if (spawn.getPreference() > 0) //add the chunk if we like its spawn chances
            chunkSpawns.add(spawn);
    }

    public List<ChunkSpawn> getChunkSpawns() {
        return chunkSpawns;
    }

    public List<ChunkSpawn> getShuffledSpawns() {
        for (ChunkSpawn spawn : chunkSpawns) {
            spawn.setPreferenceOffset(Math.random());
        }
        Collections.sort(chunkSpawns);
        return chunkSpawns;
    }
}
