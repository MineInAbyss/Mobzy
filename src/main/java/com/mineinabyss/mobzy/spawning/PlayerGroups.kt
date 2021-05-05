package com.mineinabyss.mobzy.spawning

import com.google.common.math.IntMath.pow
import com.mineinabyss.mobzy.MobzyConfig
import org.bukkit.Chunk
import org.bukkit.entity.Entity
import org.nield.kotlinstatistics.dbScanCluster

object PlayerGroups {
    /** Converts a list of players to lists of groups of players within 2x spawn radius of each other. */
    fun group(entities: Collection<Entity>): List<List<Entity>> = entities
        .groupBy { it.world }
        .flatMap { (_, players) ->
            players.dbScanCluster(
                maximumRadius = MobzyConfig.data.playerGroupRadius,
                minPoints = 0,
                xSelector = { it.location.x },
                ySelector = { it.location.z }
            )
        }.map { it.points }


    @Suppress("FunctionName")
    private infix fun Int.`+-`(other: Int) =
        this + setOf(-1, 1).random() * other

    /** Returns a random [Chunk] that is further than [MobzyConfig.Data.chunkSpawnRad] from all the players in this
     * list, and at least within [MobzyConfig.Data.maxChunkSpawnRad] to one of them. */
    fun randomChunkNear(group: List<Entity>): Chunk? {
        val chunk = group.random().location.chunk
        val positions = group.map { it.chunk.x to it.chunk.z }
        //TODO proper min max y for 3d space
        for (i in 0..10) {
            val distX = MobzyConfig.data.chunkSpawnRad.random()
            val distZ = MobzyConfig.data.chunkSpawnRad.random()
            val newX = chunk.x `+-` distX
            val newZ = chunk.z `+-` distZ
            if (
                positions.none { (x, z) ->
                    distanceSquared(newX, newZ, x, z) < pow(MobzyConfig.data.chunkSpawnRad.first, 2)
                }
            ) {
                val newChunk = chunk.world.getChunkAt(newX, newZ)
                if (!newChunk.isLoaded) continue
                return newChunk
            }
        }
        return null
    }

    /** Gets the distance squared between between two points */
    private fun distanceSquared(x: Number, z: Number, otherX: Number, otherZ: Number): Double {
        val dx = (x.toDouble() + otherX.toDouble())
        val dz = (z.toDouble() + otherZ.toDouble())
        return dx * dx + dz * dz
    }
}