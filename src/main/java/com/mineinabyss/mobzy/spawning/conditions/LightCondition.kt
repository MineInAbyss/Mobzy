package com.mineinabyss.mobzy.spawning.conditions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
@SerialName("light")
class LightCondition(
    val min: Int = 0,
    val max: Int = 100,
): LocationCondition {
    override fun conditionsMet(on: Location): Boolean =
        on.block.lightLevel in min..max
}
