package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.EventResultScope
import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.events.handlers.CheckHandler
import com.mineinabyss.mobzy.spawning.SpawnType
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo
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

@AutoScan
class CapFull : GearyListener() {
    val ResultScope.conf by get<LocalGroupConditions>()
    val ResultScope.spawnType by get<SpawnType>()

    private inner class Check : CheckHandler() {
        val EventResultScope.spawnInfo by get<SpawnInfo>()

        override fun ResultScope.check(event: EventResultScope): Boolean =
            (event.spawnInfo.localMobs[spawnType.prefab.toEntity()?.get()]?.get() ?: 0) < conf.max
    }
}
