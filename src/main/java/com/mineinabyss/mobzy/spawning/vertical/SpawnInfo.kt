package com.mineinabyss.mobzy.spawning.vertical

import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.spawning.SpawnDefinition.SpawnPosition
import org.bukkit.Location
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

//TODO name could be confused with SpawnRegion
/**
 * Defines a vertical area from a top and bottom location, with information about its gap
 *
 * @property top The topmost location of the spawn.
 * @property bottom The bottommost location of the spawn.
 * @property gap The gap in the y axis between the two of them.
 */
data class SpawnInfo(
    val bottom: Location,
    val top: Location,
    val searchRadius: Int = 5
) {
    val localMobs: Map<NMSEntityType<*>, AtomicInteger> by lazy {
        val map = mutableMapOf<NMSEntityType<*>, AtomicInteger>()
        val world = bottom.world
        val x = bottom.chunk.x
        val z = bottom.chunk.z
        for (i in -searchRadius..searchRadius) for (j in -searchRadius..searchRadius) {
            for (entity in world.getChunkAt(x + i, z + j).entities) {
                map.getOrPut(entity.toNMS().entityType, { AtomicInteger() }).incrementAndGet()
            }
        }
        map
    }

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
    fun getSpawnFor(position: SpawnPosition): Location =
        when (position) {
            //pick some position between the bottom and top when spawn position is in air
            SpawnPosition.AIR -> bottom.clone().apply { if (gap > 1) y = Random.nextInt(gap - 1).toDouble() }
            SpawnPosition.GROUND -> bottom.clone()
            SpawnPosition.OVERHANG -> top.clone()
        }

    override fun toString(): String = "SpawnArea: $bottom, $top"
}
