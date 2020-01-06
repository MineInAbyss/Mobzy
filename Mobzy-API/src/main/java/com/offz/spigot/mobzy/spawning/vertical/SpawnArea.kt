package com.offz.spigot.mobzy.spawning.vertical

import com.offz.spigot.mobzy.spawning.MobSpawn.SpawnPosition
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
class SpawnArea(val top: Location, val bottom: Location) {
    //adding one since if the blocks are on the same block, they still have a gap of 1 from top to bottom
    val gap: Int = top.blockY - bottom.blockY + 1

    fun getSpawnLocation(spawnPosition: SpawnPosition): Location =
            when (spawnPosition) {
                //pick some position between the bottom and top when spawn position is in air
                SpawnPosition.AIR -> bottom.clone().add(0.0, Random.nextDouble(gap.toDouble()), 0.0)
                SpawnPosition.GROUND -> bottom
                else -> top
            }

    override fun toString(): String = "SpawnArea: " + bottom.blockY + ", " + top.blockY
}