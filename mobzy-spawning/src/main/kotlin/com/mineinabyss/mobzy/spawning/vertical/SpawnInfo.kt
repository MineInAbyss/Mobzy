package com.mineinabyss.mobzy.spawning.vertical

import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.query.Query
import com.mineinabyss.idofront.location.down
import com.mineinabyss.idofront.location.up
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.spawning.SpawnPosition
import com.mineinabyss.mobzy.spawning.components.SubChunkBlockComposition
import org.bukkit.ChunkSnapshot
import org.bukkit.Location
import org.bukkit.entity.Entity
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
class SpawnInfo(
    val bottom: Location,
    val top: Location,
    searchRadius: Double = 200.0,
    chunkSnapshot: ChunkSnapshot? = null,
) {
    val chunkSnapshot: ChunkSnapshot by lazy { chunkSnapshot ?: bottom.chunk.chunkSnapshot }

    val blockComposition by lazy { SubChunkBlockComposition(this.chunkSnapshot, bottom.blockY) }

    object NearbyQuery : Query() {
        val ResultScope.bukkit by get<BukkitEntity>()
    }

    private val searchRadiusSquared = searchRadius * searchRadius

    //TODO more efficiently finding all ECS entities nearby
    val localMobs: Map<NMSEntityType<*>, AtomicInteger> by lazy {
        NearbyQuery.run {
            map { it.bukkit }.filter {
                it.location.world == bottom.world && it.location.distanceSquared(
                    bottom
                ) < searchRadiusSquared
            }
        }.categorizeMobs()
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
}

fun Collection<Entity>.categorizeMobs(): Map<NMSEntityType<*>, AtomicInteger> {
    val map = mutableMapOf<NMSEntityType<*>, AtomicInteger>()
    forEach { entity ->
        map.getOrPut(entity.toNMS().entityType) { AtomicInteger() }.incrementAndGet()
    }
    return map
}
