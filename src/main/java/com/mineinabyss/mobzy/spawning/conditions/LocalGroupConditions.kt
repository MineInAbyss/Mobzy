package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
@SerialName("group")
class LocalGroupConditions(
    val max: Int,
    val radius: Double
): LocationCondition {
    override fun conditionsMet(on: Location): Boolean {
        val localSpawns =  on.getNearbyEntities(radius, radius, radius).count {
            it.toNMS().entityType == null
        }

        return localSpawns < max
    }
}
