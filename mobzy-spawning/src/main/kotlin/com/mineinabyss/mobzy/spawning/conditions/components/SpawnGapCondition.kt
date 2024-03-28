package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.serialization.serializers.InnerSerializer
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * # `mobzy:spawn.gap`
 *
 * Ensures that when a mob spawn happens, the gap of air blocks is within a [range] of heights.
 */
@Serializable(with = SpawnGap.Serializer::class)
class SpawnGap(
    @Serializable(with = IntRangeSerializer::class)
    val range: IntRange
) {
    class Serializer : InnerSerializer<IntRange, SpawnGap>(
        "mobzy:check.spawn.gap",
        IntRangeSerializer,
        { SpawnGap(it) },
        { it.range },
    )
}

@AutoScan
fun GearyModule.spawnGroupChecker() = listener(object : ListenerQuery() {
    val spawnGap by source.get<SpawnGap>()

    val spawnInfo by event.get<SpawnInfo>()
}).check {
    spawnInfo.gap in spawnGap.range
}
