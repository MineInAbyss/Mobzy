package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
@SerialName("gap")
class SpawnGapCondition(
    @Serializable(with = IntRangeSerializer::class)
    val range: IntRange
) : GearyCondition() {
    val GearyEntity.location by get<Location>()
    val GearyEntity.spawnInfo by get<SpawnInfo>()

    override fun GearyEntity.check(): Boolean =
        spawnInfo.gap in range
}
