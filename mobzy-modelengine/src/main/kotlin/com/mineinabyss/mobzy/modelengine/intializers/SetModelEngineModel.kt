package com.mineinabyss.mobzy.modelengine.intializers

import com.mineinabyss.idofront.serialization.DoubleRangeSerializer
import com.mineinabyss.idofront.util.DoubleRange
import com.ticxo.modelengine.api.entity.CullType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:set.modelengine-model")
class SetModelEngineModel(
    val modelId: String,
//    val hitbox: Boolean = true,
    val invisible: Boolean = true,
    val damageTint: Boolean = true,
    val nametag: Boolean = true,
    val stepHeight: Double = 0.0,
    val scale: @Serializable(DoubleRangeSerializer::class) DoubleRange = 1.0..1.0,
    val verticalCull: VerticalCull? = null,
    val backCull: BackCull? = null,
    val blockedCull: BlockedCull? = null,
) {

    @Serializable
    data class VerticalCull(
        val type: CullType,
        val distance: Double,
        val ignoreRadius: Double
    )

    @Serializable
    data class BackCull(
        val type: CullType,
        val angle: Double,
        val ignoreRadius: Double
    )

    @Serializable
    data class BlockedCull(
        val type: CullType,
        val angle: Double,
        val ignoreRadius: Double
    )
}
