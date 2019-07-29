package com.offz.spigot.mobzy.Spawning;

import com.offz.spigot.mobzy.Spawning.Vertical.SpawnArea;
import com.offz.spigot.mobzy.Spawning.Vertical.VerticalSpawn;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.List;

public class ChunkSpawn implements Comparable {
    private double preference = 1; //introduce a little noise into our preference so we don't end up with a specific order of chunks spawning entities
    private double preferenceOffset = 0; //
    private Chunk chunk;
    private int minY;
    private int maxY;
    public ChunkSpawn(Chunk chunk, int minY, int maxY) {
        this.chunk = chunk;
        this.minY = minY;
        this.maxY = maxY;
        calculatePreference();
    }

    public void setPreferenceOffset(double preferenceOffset) {
        this.preferenceOffset = preferenceOffset;
    }

    public double getPreference() {
        return preference + preferenceOffset;
    }

    /**
     * Calculates a weight for how much we think we'll like this chunk for spawns. Looks at whether randomly picked block
     * was void.
     * TODO this could have more complex checks in the future
     *
     * @return our predicted preference towards this chunk
     */
    private void calculatePreference() {
        //pick random block in chunk
        if (VerticalSpawn.getHighestBlock(randomLocInChunk(), minY, maxY).getBlockY() == minY) //if we found void
            preference = 0;
    }

    private Location randomLocInChunk() {
        return chunk.getBlock(((int) (Math.random() * 15)), 0, ((int) (Math.random() * 15))).getLocation();
    }

    private List<SpawnArea> generateSpawnAreas() {
        //STEP 1: Randomly pick location in chunk
        VerticalSpawn spawn = new VerticalSpawn(randomLocInChunk(), minY, maxY);
        return spawn.getSpawnAreas();
    }

    public SpawnArea getSpawnArea(int tries) {
        for (int i = 0; i < tries; i++) {
            //generate list of spawn areas
            List<SpawnArea> spawnAreas = generateSpawnAreas();
            if (spawnAreas.isEmpty()) //if there were no blocks there, try again
                continue;

            //weighted choice based on gap size
            RandomCollection<SpawnArea> weightedChoice = new RandomCollection<>();
            for (SpawnArea spawnArea : spawnAreas) { //add each
                int weight = spawnArea.getGap();
                if (weight > 100)
                    weight = 100; //make underground spawns a little more likely, TODO could be a more complex function eventually
                weightedChoice.add(weight, spawnArea);
            }
            return weightedChoice.next(); //pick one
        }
        return null;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof ChunkSpawn))
            return 0;
        return ((int) (getPreference() - ((ChunkSpawn) o).getPreference()));
    }
}
