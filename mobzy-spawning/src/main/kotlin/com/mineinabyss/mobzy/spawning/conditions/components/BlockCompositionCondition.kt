package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.EventScope
import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.get
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.api.systems.Handler
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
class BlockCompositionCondition : GearyListener() {
    private val TargetScope.blockComposition by get<BlockComposition>()

    val EventScope.spawnInfo by get<SpawnInfo>()

    @Handler
    fun TargetScope.check(event: EventScope): Boolean =
        blockComposition.materials.all { (material, range) ->
            event.spawnInfo.blockComposition.percent(material) in range
        }
}
