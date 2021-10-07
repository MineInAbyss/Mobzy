package com.mineinabyss.mobzy.spawning

import be.seeseemelk.mockbukkit.ChunkMock
import io.mockk.*
import org.bukkit.Chunk
import org.bukkit.ChunkSnapshot

object Helpers {
    fun Chunk.mockSnapshot() =
        spyk(this) {
            every { chunkSnapshot } answers {
                val chunk = this@spyk
                mockkClass(ChunkSnapshot::class) snapshot@{
                    every { x } answers { chunk.x }
                    every { z } answers { chunk.z }
                    every { getBlockType(any(), any(),any()) } answers {
                        chunk.getBlock(arg<Int>(0), arg<Int>(1), arg<Int>(2)).type
                    }
                }
            }
    }
}
