package com.mineinabyss.mobzy.spawning

import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.idofront.time.ticks
import kotlinx.serialization.Serializable
import org.bukkit.entity.SpawnCategory
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
    val chunkSpawnRad: IntRange = 2..4,
    val maxCommandSpawns: Int = 20,
    val playerGroupRadius: Double = 96.0,
    @Serializable(with = DurationSerializer::class)
    val spawnTaskDelay: Duration = 40.ticks,
    val creatureTypeCaps: MutableMap<SpawnCategory, Int> = mutableMapOf(
        SpawnCategory.ANIMAL to 60,
        SpawnCategory.MONSTER to 55,
        SpawnCategory.WATER_ANIMAL to 30,
        SpawnCategory.AMBIENT to 0
    ),
    val spawnHeightRange: Int = 40,
) {
    /** @return The spawn cap for that mob in config. */
    fun getCreatureTypeCap(creatureType: SpawnCategory): Int = creatureTypeCaps[creatureType] ?: 0
}

