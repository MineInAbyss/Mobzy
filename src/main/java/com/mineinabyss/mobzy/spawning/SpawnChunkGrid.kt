package com.mineinabyss.mobzy.spawning

import org.bukkit.Chunk
import org.bukkit.Location

/**
 * A collection of [ChunkSpawn]s
 */
class SpawnChunkGrid internal constructor(locs: List<Location>, minRad: Int, maxRad: Int) {
    val validChunkSpawns: MutableList<ChunkSpawn>
    val allChunks: List<Chunk>

    init {
        val invalidChunks = mutableListOf<Chunk>()
        val validChunks = mutableListOf<Chunk>()
        var minY = locs[0].blockY
        var maxY = minY
        for (loc in locs) {
            runInRadius(maxRad) { x, z, dist ->
                //get a min and max y position from all player positions
                val y = loc.blockY
                if (y < minY) minY = y
                if (y > maxY) maxY = y
                //if we are within the maximum circular radius
                val spawnChunk = loc.world!!.getChunkAt(loc.chunk.x + x, loc.chunk.z + z)
                //only add to valid chunks if we're outside of minRange and it hasn't already been added to
                // invalid chunks (is too close to another player)
                if (dist > minRad * minRad) {
                    if (!validChunks.contains(spawnChunk) && !invalidChunks.contains(spawnChunk))
                        validChunks.add(spawnChunk)
                } else if (!invalidChunks.contains(spawnChunk))
                    invalidChunks.add(spawnChunk)

            }
        }

        allChunks = validChunks.plus(invalidChunks)

        //convert chunks to only valid ChunkSpawns
        validChunkSpawns = validChunks
                .map { ChunkSpawn(it, 0, 254) }
                .filter { isValidSpawn(it) }
                .toMutableList()

        fun addSpawnIfValid(spawn: ChunkSpawn) {
            if (isValidSpawn(spawn)) //add the chunk if we like its spawn chances
                validChunkSpawns.add(spawn)
        }

        for (chunk in invalidChunks) {
            val minVertical = minRad * 16 //minimum vertical spawn distance is the number of chunks * width of a chunk
            //do some checks to add areas above and below the player when we've reached inside the minimum radius
            if (maxY + minVertical < 254) addSpawnIfValid(ChunkSpawn(chunk, maxY + minVertical, 254))
            if (minY - minVertical > 0) addSpawnIfValid(ChunkSpawn(chunk, 0, minY - minVertical))
        }
    }

    private fun isValidSpawn(spawn: ChunkSpawn) = spawn.preference > 0

    /**
     * Slightly shuffles the list of ChunkSpawns, with some consideration for their original preferences
     */
    fun shuffledSpawns(): List<ChunkSpawn> {
        validChunkSpawns.forEach { it.preferenceOffset = Math.random() }
        return sortedSpawns()
    }

    private fun sortedSpawns() = validChunkSpawns.sortedBy { it.preference }
}

fun runInRadius(radius: Int, forEach: (x: Int, z: Int, dist: Int) -> Unit) {
    for (x in -radius..radius) for (z in -radius..radius) {
        val dist = x * x + z * z
        if (dist <= radius * radius)
            forEach(x, z, dist)
    }
}