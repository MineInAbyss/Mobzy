package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.EventScope
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo
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

@AutoScan
class SpawnGapCondition : GearyListener() {
    private val TargetScope.spawnGap by get<SpawnGap>()

    private val EventScope.spawnInfo by get<SpawnInfo>()

    @Handler
    fun TargetScope.check(event: EventScope): Boolean =
        event.spawnInfo.gap in spawnGap.range
}
