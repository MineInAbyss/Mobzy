package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.systems.GearyHandlerScope
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.mobzy.spawning.SpawnType
import com.mineinabyss.mobzy.spawning.conditions.onCheckSpawn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * # `mobzy:spawn.local_group`
 *
 * Checks that no more than [max] Bukkit entities of the same type as this one are within [radius] blocks during a
 * mob spawn.
 */
@Serializable
@SerialName("mobzy:check.spawn.local_group")
class LocalGroupConditions(
    val max: Int,
    val radius: Double
)

object CapFull : GearyListener() {
    val ResultScope.conf by get<LocalGroupConditions>()
    val ResultScope.spawnType by get<SpawnType>()

    override fun GearyHandlerScope.register() {
        onCheckSpawn { spawnInfo ->
            (spawnInfo.localMobs[spawnType.prefab.toEntity()?.get()]?.get() ?: 0) < conf.max
        }
    }
}
