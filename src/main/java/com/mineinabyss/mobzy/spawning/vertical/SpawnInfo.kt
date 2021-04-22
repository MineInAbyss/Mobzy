package com.mineinabyss.mobzy.spawning.vertical

import com.mineinabyss.idofront.nms.entity.creatureType
import com.mineinabyss.mobzy.MobzyConfig
import com.mineinabyss.mobzy.spawning.SpawnDefinition
import com.mineinabyss.mobzy.spawning.SpawnDefinition.SpawnPosition
import org.bukkit.Location
import org.bukkit.entity.Entity
import kotlin.random.Random

//TODO name could be confused with SpawnRegion
/**
 * Defines a vertical area from a top and bottom location, with information about its gap
 *
 * @property top The topmost location of the spawn.
 * @property bottom The bottommost location of the spawn.
 * @property gap The gap in the y axis between the two of them.
 */
class SpawnInfo(
    val bottom: Location,
    val top: Location,
) {
    //adding one since if the blocks are on the same block, they still have a gap of 1 from top to bottom
    val gap: Int = top.blockY - bottom.blockY + 1

    override fun toString(): String = "SpawnArea: $bottom, $top"
}
