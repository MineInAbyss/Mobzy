package com.mineinabyss.mobzy

import com.mineinabyss.idofront.plugin.getService
import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.mobzy.ecs.components.MobCategory
import kotlinx.serialization.Serializable
import org.bukkit.command.CommandSender
import kotlin.time.Duration

interface MobzyConfig {
    companion object : MobzyConfig by getService()

    /**
     * @property debug whether the plugin is in a debug state (used primarily for broadcasting messages)
     * @property doMobSpawns whether custom mob spawning enabled
     * @property chunkSpawnRad the minimum number of chunks away from the player in which a mob can spawn
     * @property maxChunkSpawnRad the maximum number of chunks away from the player in which a mob can spawn
     * @property maxCommandSpawns the maximum number of mobs to spawn with /mobzy spawn
     * @property playerGroupRadius the radius around which players will count mobs towards the local mob cap
     * @property spawnTaskDelay the delay in ticks between each attempted mob spawn
     * @property creatureTypeCaps Per-player mob caps for spawning of [NMSCreatureType]s on the server.
     * @property spawnHeightRange The maximum amount above or below players that mobs can spawn.
     */
    @Serializable
    class Data(
        var debug: Boolean = false,
        var doMobSpawns: Boolean = false,
        var supportNonMEEntities: Boolean = false,
        @Serializable(with = IntRangeSerializer::class)
        var chunkSpawnRad: IntRange,
        var maxCommandSpawns: Int,
        var playerGroupRadius: Double,
        @Serializable(with = DurationSerializer::class)
        var spawnTaskDelay: Duration,
        var creatureTypeCaps: MutableMap<MobCategory, Int> = mutableMapOf(),
        var spawnHeightRange: Int,
    )

    val data: Data

    fun getCreatureTypeCap(creatureType: MobCategory): Int

    //TODO interface for IdofrontConfig
    fun reload(sender: CommandSender)
}
