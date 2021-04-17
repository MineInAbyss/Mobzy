package com.mineinabyss.mobzy.spawning.conditions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
@SerialName("light")
class HeightCondition(
    val min: Int = 0,
    val max: Int = 256,
): LocationCondition {
    override fun conditionsMet(on: Location): Boolean =
        on.y.toInt() in min..max
}
