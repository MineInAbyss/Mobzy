package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.idofront.serialization.IntRangeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
@SerialName("light")
class LightCondition(
    @Serializable(with = IntRangeSerializer::class)
    val range: IntRange = 0..15,
) : GearyCondition() {
    val GearyEntity.location by get<Location>()

    override fun GearyEntity.check(): Boolean =
        location.block.lightLevel in range
}
