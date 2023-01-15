package com.mineinabyss.mobzy.spawning

import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.mobzy.ecs.components.MobCategory
import kotlinx.serialization.Serializable
import kotlin.time.Duration

/**
 * @property chunkSpawnRad the minimum number of chunks away from the player in which a mob can spawn
 * @property maxCommandSpawns the maximum number of mobs to spawn with /mobzy spawn
 * @property playerGroupRadius the radius around which players will count mobs towards the local mob cap
 * @property spawnTaskDelay the delay in ticks between each attempted mob spawn
 * @property creatureTypeCaps Per-player mob caps for spawning of [NMSCreatureType]s on the server.
 * @property spawnHeightRange The maximum amount above or below players that mobs can spawn.
 */
@Serializable
class SpawnConfig(
    @Serializable(with = IntRangeSerializer::class)
    val chunkSpawnRad: IntRange,
    val maxCommandSpawns: Int,
    val playerGroupRadius: Double,
    @Serializable(with = DurationSerializer::class)
    val spawnTaskDelay: Duration,
    val creatureTypeCaps: MutableMap<MobCategory, Int> = mutableMapOf(),
    val spawnHeightRange: Int,
) {
    /**
     * @param creatureType The name of the [EnumCreatureType].
     * @return The mob cap for that mob in config.
     */
    fun getCreatureTypeCap(creatureType: MobCategory): Int = creatureTypeCaps[creatureType] ?: 0
}

