package com.offz.spigot.mobzy.Spawning;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SpawnChunkGrid {
    private List<ChunkSpawn> chunkSpawns;


    SpawnChunkGrid(List<Location> locs, int minRad, int maxRad) {
        List<Chunk> tooCloseChunks = new ArrayList<>();
        List<Chunk> spawnableChunks = new ArrayList<>();
        int minY = locs.get(0).getBlockY(), maxY = minY;
        for (Location loc : locs) {
            //get a min and max y position from all player positions
            int y = loc.getBlockY();
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;

            Chunk chunk = loc.getChunk();
            int startX = chunk.getX();
            int startZ = chunk.getZ();
            World world = loc.getWorld();

            for (int x = -maxRad; x <= maxRad; x++) {
                for (int z = -maxRad; z <= maxRad; z++) {
                    double dist = x * x + z * z;
                    //if we are within the maximum circular radius and not within minimum, add the chunk to the list
                    if (dist <= maxRad * maxRad) {
                        Chunk spawnChunk = world.getChunkAt(startX + x, startZ + z);
                        if (dist > minRad * minRad) {
                            if (!spawnableChunks.contains(spawnChunk))
                                spawnableChunks.add(spawnChunk);
                        } else if (!tooCloseChunks.contains(spawnChunk))
                            tooCloseChunks.add(spawnChunk);
                    }
                }
            }
        }

        chunkSpawns = spawnableChunks.stream()
                .filter(chunk -> !tooCloseChunks.contains(chunk))
                .map(spawnChunk -> new ChunkSpawn(spawnChunk, 0, 254))
                .filter(this::isValidSpawn)
                .collect(Collectors.toList());

        for (Chunk chunk : tooCloseChunks) {
            int minVertical = minRad * 16; //minimum vertical spawn distance is the number of chunks * width of a chunk
            //do some checks to add areas above and below the player when we've reached inside the minimum radius
            if (maxY + minVertical < 254)
                addSpawnIfValid(new ChunkSpawn(chunk, maxY + minVertical, 254));
            if (minY - minVertical > 0)
                addSpawnIfValid(new ChunkSpawn(chunk, 0, minY - minVertical));
        }
    }

    private void addSpawnIfValid(ChunkSpawn spawn) {
        if (isValidSpawn(spawn)) //add the chunk if we like its spawn chances
            chunkSpawns.add(spawn);
    }

    private boolean isValidSpawn(ChunkSpawn spawn) {
        return spawn.getPreference() > 0;
    }

    public List<ChunkSpawn> getChunkSpawns() {
        return chunkSpawns;
    }

    public List<ChunkSpawn> getShuffledSpawns() {
        for (ChunkSpawn spawn : chunkSpawns)
            spawn.setPreferenceOffset(Math.random());

        chunkSpawns.sort(Comparator.comparing(ChunkSpawn::getPreference));
        return chunkSpawns;
    }
}
