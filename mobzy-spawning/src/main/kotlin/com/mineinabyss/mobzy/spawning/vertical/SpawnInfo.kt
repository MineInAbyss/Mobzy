package com.mineinabyss.mobzy.spawning.vertical

import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.helpers.prefabs
import com.mineinabyss.idofront.location.down
import com.mineinabyss.idofront.location.up
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.spawning.SpawnPosition
import com.mineinabyss.mobzy.spawning.components.SubChunkBlockComposition
import org.bukkit.ChunkSnapshot
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.SpawnCategory
import kotlin.random.Random

//TODO name could be confused with SpawnRegion
/**
 * Defines a vertical area from a top and bottom location, with information about its gap
 *
 * @property top The topmost location of the spawn.
 * @property bottom The bottommost location of the spawn.
 * @property gap The gap in the y-axis between the two of them.
 */
class SpawnInfo(
    val bottom: Location,
    val top: Location,
    searchRadius: Double = 400.0,
    chunkSnapshot: ChunkSnapshot? = null,
) {
    val chunkSnapshot: ChunkSnapshot by lazy { chunkSnapshot ?: bottom.chunk.chunkSnapshot }

    val blockComposition by lazy { SubChunkBlockComposition(this.chunkSnapshot, bottom.blockY) }

    private val searchRadiusSquared = searchRadius * searchRadius

    val localMobs: Collection<BukkitEntity> by lazy {
        bottom.getNearbyEntities(searchRadiusSquared, searchRadiusSquared, searchRadiusSquared)
    }
    val localTypes: Map<PrefabKey?, Int> by lazy { categorizeByType(localMobs) }
    val localCategories: Map<SpawnCategory, Int> by lazy {
        localMobs.groupingBy { it.spawnCategory }.eachCount()
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
            SpawnPosition.AIR -> bottom.clone().apply { if (gap > 1) y += Random.nextInt(gap - 1).toDouble() }
            SpawnPosition.GROUND -> bottom.clone().up(1)
            SpawnPosition.OVERHANG -> top.clone().down(1)
        }

    override fun toString(): String = "SpawnInfo: $bottom, $top"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpawnInfo

        if (bottom != other.bottom) return false
        if (top != other.top) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bottom.hashCode()
        result = 31 * result + top.hashCode()
        return result
    }

    companion object {
        //TODO perhaps give normal mobs prefab keys too to make this more type safe
        fun categorizeByType(mobs: Collection<Entity>): Map<PrefabKey?, Int> =
            mobs.groupingBy { it.toGearyOrNull()?.prefabs?.firstOrNull()?.get<PrefabKey>() }.eachCount()
    }
}

