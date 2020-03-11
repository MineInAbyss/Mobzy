package com.mineinabyss.mobzy.spawning

import com.mineinabyss.mobzy.spawning.vertical.SpawnArea
import com.mineinabyss.mobzy.spawning.vertical.VerticalSpawn
import com.mineinabyss.mobzy.spawning.vertical.getHighestBlock
import org.bukkit.Chunk

/**
 *
 *
 * @property preference Increases the likelihood of this spawn to be chosen out of a list of other candidates.
 * @property truePreference The [preference] calculated internally without the [preferenceOffset].
 * @property preferenceOffset Introduces noise into our preference so we don't end up with a specific order of chunks
 * spawning entities (e.x. everything spawning in one corner).
 * @property randomLocInChunk A random location with y of 0 in the chunk. Only calculated once.
 * @property spawnAreas The spawn areas within the [VerticalSpawn] located at [randomLocInChunk]
 */
class ChunkSpawn(private val chunk: Chunk, private val minY: Int, private val maxY: Int) {
    val preference
        get() = truePreference + preferenceOffset
    private var truePreference = 1.0
    var preferenceOffset = 0.0
    private val randomLocInChunk = chunk.getBlock((Math.random() * 15).toInt(), 0, (Math.random() * 15).toInt()).location
    private val spawnAreas = VerticalSpawn(randomLocInChunk, minY, maxY).spawnAreas

    init {
        calculatePreference()
    }

    /**
     * Calculates a weight for how much we think we'll like this chunk for spawns. Looks at whether randomly picked block
     * was void.
     * TODO this could have more complex checks in the future
     */
    private fun calculatePreference() { //pick random block in chunk
        if (randomLocInChunk.getHighestBlock(minY, maxY).blockY == minY) //if we found void
            truePreference = 0.0
    }

    fun getSpawnArea(tries: Int): SpawnArea? {
        for (i in 0 until tries) { //generate list of spawn
            if (spawnAreas.isEmpty()) //if there were no blocks there, try again
                continue
            //weighted choice based on gap size
            val weightedChoice = RandomCollection<SpawnArea>()
            for (spawnArea in spawnAreas) { //add each
                var weight = spawnArea.gap
                if (weight > 100) weight = 100 //make underground spawns a little more likely, TODO could be a more complex function eventually
                weightedChoice.add(weight.toDouble(), spawnArea)
            }
            return weightedChoice.next() //pick one
        }
        return null
    }
}