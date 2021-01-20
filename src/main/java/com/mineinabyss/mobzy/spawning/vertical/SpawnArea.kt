package com.mineinabyss.mobzy.spawning.vertical

import com.mineinabyss.mobzy.spawning.MobSpawn
import com.mineinabyss.mobzy.spawning.MobSpawn.SpawnPosition
import org.bukkit.Location
import kotlin.random.Random

//TODO name could be confused with SpawnRegion
/**
 * Defines a vertical area from a top and bottom location, with information about its gap
 *
 * @property top The topmost location of the spawn.
 * @property bottom The bottommost location of the spawn.
 * @property gap The gap in the y axis between the two of them.
 */
class SpawnArea(
    val top: Location,
    val bottom: Location
) {
    //adding one since if the blocks are on the same block, they still have a gap of 1 from top to bottom
    val gap: Int = top.blockY - bottom.blockY + 1

    /**
     * Get a [Location] to spawn in depending on the specified [SpawnPosition]
     *
     * @return
     * - If [SpawnPosition.AIR], a random location between [top] and [bottom]
     * - If [SpawnPosition.GROUND], [bottom]
     * - If [SpawnPosition.OVERHANG], [top]
     */
    fun getSpawnFor(mobSpawn: MobSpawn): Location =
        when (mobSpawn.spawnPos) {
            //pick some position between the bottom and top when spawn position is in air
            SpawnPosition.AIR -> bottom.clone().apply { if (gap > 1) y = Random.nextInt(gap - 1).toDouble() }
            SpawnPosition.GROUND -> bottom.clone()
            SpawnPosition.OVERHANG -> top.clone()
        }

    override fun toString(): String = "SpawnArea: " + bottom.blockY + ", " + top.blockY
}
