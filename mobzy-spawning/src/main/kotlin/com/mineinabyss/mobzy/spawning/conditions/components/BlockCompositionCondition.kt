package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.serialization.DoubleRangeSerializer
import com.mineinabyss.idofront.util.DoubleRange
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

/**
 * # `mobzy:spawn.gap`
 *
 * Ensures that when a mob spawn happens, the gap of air blocks is within a [range] of heights.
 */
@Serializable
@SerialName("mobzy:check.spawn.composition")
class BlockComposition(
    val materials: Map<Material, @Serializable(with = DoubleRangeSerializer::class) DoubleRange>
)

@AutoScan
fun GearyModule.blockCompositionChecker() = listener(object : ListenerQuery() {
    val blockComposition by source.get<BlockComposition>()
    val spawnInfo by event.get<SpawnInfo>()
}).check {
    blockComposition.materials.all { (material, range) ->
        spawnInfo.blockComposition.percent(material) in range
    }
}
