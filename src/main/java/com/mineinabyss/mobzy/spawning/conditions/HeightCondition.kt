package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
@SerialName("height")
class HeightCondition(
    val min: Int = 0,
    val max: Int = 256,
) : GearyCondition() {
    val GearyEntity.location by get<Location>()

    override fun GearyEntity.check(): Boolean =
        location.y.toInt() in min..max
}
