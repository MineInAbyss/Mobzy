package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.systems.GearyHandlerScope
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.idofront.serialization.DoubleRangeSerializer
import com.mineinabyss.idofront.util.DoubleRange
import com.mineinabyss.mobzy.spawning.conditions.onCheckSpawn
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

object BlockCompositionCondition : GearyListener() {
    private val ResultScope.blockComposition by get<BlockComposition>()

    override fun GearyHandlerScope.register() {
        onCheckSpawn { spawnInfo ->
            blockComposition.materials.all { (material, range) ->
                spawnInfo.blockComposition.percent(material) in range
            }
        }
    }
}
