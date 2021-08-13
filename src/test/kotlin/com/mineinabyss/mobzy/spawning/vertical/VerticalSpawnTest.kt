package com.mineinabyss.mobzy.spawning.vertical

import com.mineinabyss.mobzy.spawning.BukkitTest
import com.mineinabyss.mobzy.spawning.Helpers.mockSnapshot
import io.kotest.matchers.shouldBe
import org.bukkit.Material
import org.junit.jupiter.api.Test

internal class VerticalSpawnTest : BukkitTest() {
    val chunk get() = world.getChunkAt(0, 0)
    fun gap(min: Int, max: Int) = SpawnInfo(chunk.getBlock(0, min, 0).location, chunk.getBlock(0, max, 0).location)

    fun findGapAt(min: Int, max: Int, start: Int) =
        VerticalSpawn.findGap(
            chunk = chunk.mockSnapshot(),
            minY = min,
            maxY = max,
            x = 0,
            z = 0,
            startY = start
        )

    @Test
    fun findGap() {
        //World: 10 GRASS_BLOCK, AIR

        // Single gap tests
        findGapAt(0, 127, 100) shouldBe gap(10, 127)
        findGapAt(0, 127, 0) shouldBe gap(10, 127)
        findGapAt(0, 127, 127) shouldBe gap(10, 127)

        // Two gaps
        world.getBlockAt(0, 20, 0).type = Material.STONE
        findGapAt(0, 127, 15) shouldBe gap(10, 20)
        findGapAt(0, 127, 30) shouldBe gap(20, 127)

        // Find nearest gap with two blocks
        world.getBlockAt(0, 21, 0).type = Material.STONE
        findGapAt(0, 127, 20) shouldBe gap(10, 20)
        findGapAt(0, 127, 21) shouldBe gap(21, 127)

        // min/max tests
        findGapAt(15, 127, 20) shouldBe gap(15, 20)
        findGapAt(20, 40, 20) shouldBe gap(21, 40)
        findGapAt(40, 50, 45) shouldBe gap(40, 50)
    }
}
