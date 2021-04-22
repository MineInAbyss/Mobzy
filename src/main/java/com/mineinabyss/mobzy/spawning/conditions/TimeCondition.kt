package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
@SerialName("time")
class TimeCondition(
    val min: Long = -1,
    val max: Long = 10000000,
) : GearyCondition() {

    val GearyEntity.location by get<Location>()

    override fun GearyEntity.check(): Boolean {
        val time = location.world.time

        // support these two possibilities
        // ====max-----min====
        // ----min=====max----
        return if (min < max)
            time in min..max
        else
            time !in max..min
    }

}
