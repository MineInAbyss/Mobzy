package com.mineinabyss.mobzy.spawning.conditions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.Material

@Serializable
@SerialName("light")
class BlockConditions(
    val allow: Set<Material> = setOf(),
    val deny: Set<Material> = setOf()
): LocationCondition {
    override fun conditionsMet(on: Location): Boolean =
        on.add(0.0, -1.0, 0.0).block.type.let {
            (allow.isEmpty() || it in allow) && it !in deny
        }
}
