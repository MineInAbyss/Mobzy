package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.EventResultScope
import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.events.handlers.CheckHandler
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
    private val ResultScope.spawnGap by get<SpawnGap>()

    private inner class Check : CheckHandler() {
        val EventResultScope.spawnInfo by get<SpawnInfo>()

        override fun ResultScope.check(event: EventResultScope): Boolean =
            event.spawnInfo.gap in spawnGap.range
    }
}
