package com.mineinabyss.mobzy.spawning.vertical

import org.bukkit.Chunk
import org.bukkit.Location

/**
 * A vertical strip inside a chunk, with x and z located at [loc], spanning from [minY] to [maxY].
 *
 * Will generate a list of [SpawnInfo]s, from all air gaps within this vertical strip.
 */
object VerticalSpawn {
    fun findGap(
        chunk: Chunk,
        minY: Int,
        maxY: Int,
        x: Int = (0..15).random(),
        z: Int = (0..15).random(),
        startY: Int = (minY..maxY).random(),
    ): SpawnInfo {
        //TODO getting the full chunk snapshot is by far the most inefficient step
        val snapshot = chunk.chunkSnapshot
        fun Int.getBlock() = snapshot.getBlockType(x, this, z)

        val startIsEmpty = startY.getBlock().isEmpty

        class BlocLoc(val add: Int) {
            lateinit var opposite: BlocLoc
            var y = startY
            var isEmpty: Boolean = startIsEmpty
            var foundBlock = false

            fun next(): Boolean {
                if (foundBlock) return false

                val nextIsEmpty = y.getBlock().isEmpty

                when {
                    y !in (minY + 1) until maxY || isEmpty && !nextIsEmpty -> {
                        foundBlock = true
                        return false
                    }
                    !isEmpty && nextIsEmpty -> {
                        opposite.foundBlock = true
                        opposite.y = y - add
                    }
                }
                isEmpty = nextIsEmpty
                y += add
                return true
            }
        }

        val up = BlocLoc(1)
        val down = BlocLoc(-1)
        up.opposite = down
        down.opposite = up

        while (up.next() || down.next()) {
        }

        return SpawnInfo(chunk.getBlock(x, down.y, z).location, chunk.getBlock(x, up.y, z).location)
    }
}

fun Location.checkDown(maxI: Int): Location? {
    var l = clone()
    for (i in 0 until maxI) {
        l = l.add(0.0, -1.0, 0.0)
        if (l.y < 10) return null
        if (l.y >= 256) l.y = 255.0
        if (l.block.type.isSolid) return l.add(0.0, 1.0, 0.0)
    }
    return null
}

fun Location.checkUp(maxI: Int): Location? {
    var l = clone()
    for (i in 0 until maxI) {
        l = l.add(0.0, 1.0, 0.0)
        if (!l.block.type.isSolid) {
            return l
        }
        if (l.y >= 256) return null
        if (l.y < 10) l.y = 10.0
    }
    return null
}
