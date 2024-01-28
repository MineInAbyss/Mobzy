package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.events.CheckingListener
import com.mineinabyss.geary.systems.accessors.Pointers
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
class SpawnGapCondition : CheckingListener() {
    private val Pointers.spawnGap by get<SpawnGap>().on(source)

    private val Pointers.spawnInfo by get<SpawnInfo>().on(event)

    override fun Pointers.check(): Boolean =
        spawnInfo.gap in spawnGap.range
}
