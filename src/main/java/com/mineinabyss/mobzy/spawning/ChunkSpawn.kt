package com.mineinabyss.mobzy.spawning

import com.mineinabyss.mobzy.spawning.vertical.SpawnArea
import com.mineinabyss.mobzy.spawning.vertical.VerticalSpawn
import org.bukkit.Chunk
import org.nield.kotlinstatistics.WeightedDice

/**
 * Wraps around one chunk to add functionality to take
 *
 * spawning entities (e.x. everything spawning in one corner).
 * @property randomLocInChunk A random location with y of 0 in the chunk. Only calculated once.
 * @property spawnAreas The spawn areas within the [VerticalSpawn] located at [randomLocInChunk]
 */
class ChunkSpawn(private val chunk: Chunk, private val minY: Int, private val maxY: Int) : Chunk by chunk {
    private val randomLocInChunk by lazy { chunk.getBlock((Math.random() * 15).toInt(), 0, (Math.random() * 15).toInt()).location }
    private val spawnAreas by lazy { VerticalSpawn(randomLocInChunk, minY, maxY).spawnAreas }

    /** Gets a random [SpawnArea] from [spawnAreas], through a [VerticalSpawn] */
    fun getSpawnArea(): SpawnArea? {
        if (spawnAreas.isEmpty()) return null
        //weighted choice based on gap size
        val weightedChoice = WeightedDice(
                spawnAreas.associateWith { spawnArea ->
                    //make underground spawns a little more likely by limiting to 100 blocks
                    //TODO could be a more complex function eventually
                    spawnArea.gap.coerceAtMost(100).toDouble()
                }.filterValues { it > 0 }
        )

        return weightedChoice.roll() //pick one
    }
}