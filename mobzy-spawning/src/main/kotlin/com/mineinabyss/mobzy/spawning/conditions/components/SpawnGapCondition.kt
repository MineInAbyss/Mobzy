package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.systems.GearyHandlerScope
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.mobzy.spawning.conditions.onCheckSpawn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * # `mobzy:spawn.gap`
 *
 * Ensures that when a mob spawn happens, the gap of air blocks is within a [range] of heights.
 */
@Serializable
@SerialName("mobzy:check.spawn.gap")
class SpawnGap(
    @Serializable(with = IntRangeSerializer::class)
    val range: IntRange
)

object SpawnGapCondition : GearyListener() {
    private val ResultScope.spawnGap by get<SpawnGap>()

    override fun GearyHandlerScope.register() {
        onCheckSpawn { spawnInfo ->
            spawnInfo.gap in spawnGap.range
        }
    }
}
