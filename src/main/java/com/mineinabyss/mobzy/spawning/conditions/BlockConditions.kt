package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.Material

@Serializable
@SerialName("block_type")
class BlockConditions(
    val allow: Set<Material> = setOf(),
    val deny: Set<Material> = setOf()
): GearyCondition() {
    val GearyEntity.location by get<Location>()

    override fun GearyEntity.check(): Boolean =
        location.add(0.0, -1.0, 0.0).block.type.let {
            (allow.isEmpty() || it in allow) && it !in deny
        }
}
