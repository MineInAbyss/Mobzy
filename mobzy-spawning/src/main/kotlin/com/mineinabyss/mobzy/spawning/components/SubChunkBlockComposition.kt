package com.mineinabyss.mobzy.spawning.components

import org.bukkit.ChunkSnapshot
import org.bukkit.Material
import java.util.concurrent.atomic.AtomicInteger

class SubChunkBlockComposition(
    val chunkSnapshot: ChunkSnapshot,
    val startY: Int,
) {
    val totalBlocks = 4.0 * 4.0 * 4.0
    private val blockCounts = mutableMapOf<Material, AtomicInteger>()

    init {
//         Snap to nearest sub chunk
//        val yStart = (y shr 4) * 16

        for (x in 0 until 4)
            for (y in 0 until 4)
                for (z in 0 until 4) {
                    val material = chunkSnapshot.getBlockType(x, startY + y - 8, z)
                    blockCounts.getOrPut(material) { AtomicInteger() }.getAndIncrement()
                }
    }

    operator fun get(material: Material): AtomicInteger? = blockCounts[material]

    fun percent(material: Material): Double = ((get(material)?.get() ?: 0) / totalBlocks) * 100
}
