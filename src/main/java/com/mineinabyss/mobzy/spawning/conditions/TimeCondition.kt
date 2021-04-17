package com.mineinabyss.mobzy.spawning.conditions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
@SerialName("light")
class TimeCondition(
    val min: Long = -1,
    val max: Long = 10000000,
) : LocationCondition {
    override fun conditionsMet(on: Location): Boolean {
        val time = on.world.time

        // support these two possibilities
        // ====max-----min====
        // ----min=====max----
        return if (min < max)
            time in min..max
        else
            time !in max..min
    }

}
