package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.idofront.serialization.DoubleRangeSerializer
import com.mineinabyss.idofront.util.DoubleRange
import com.mineinabyss.mobzy.spawning.components.SubChunkBlockComposition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

/**
 * # `mobzy:spawn.gap`
 *
 * Ensures that when a mob spawn happens, the gap of air blocks is within a [range] of heights.
 */
@Serializable
@SerialName("mobzy:spawn.composition")
class BlockCompositionCondition(
    val materials: Map<Material, @Serializable(with = DoubleRangeSerializer::class) DoubleRange>
) : GearyCondition() {
    private val GearyEntity.composition by get<SubChunkBlockComposition>()

    override fun GearyEntity.check(): Boolean =
        materials.all { (material, range) -> composition.percent(material) in range }

}
