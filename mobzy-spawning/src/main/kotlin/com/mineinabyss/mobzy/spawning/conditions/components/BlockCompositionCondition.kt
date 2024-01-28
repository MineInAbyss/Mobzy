package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.events.CheckingListener
import com.mineinabyss.geary.systems.accessors.Pointers
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
class BlockCompositionCondition : CheckingListener() {
    private val Pointers.blockComposition by get<BlockComposition>().on(source)
    val Pointers.spawnInfo by get<SpawnInfo>().on(event)

    override fun Pointers.check(): Boolean =
        blockComposition.materials.all { (material, range) ->
            spawnInfo.blockComposition.percent(material) in range
        }
}
