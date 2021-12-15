package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.EventResultScope
import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.events.handlers.CheckHandler
import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.mobzy.mobzyConfig
import com.mineinabyss.mobzy.spawning.GlobalSpawnInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@SerialName("mobzy:check.spawn.delay")
class SpawnDelay(
    @Serializable(with = DurationSerializer::class)
    val attemptEvery: Duration
)

@AutoScan
class SpawnDelayCondition : GearyListener() {
    val ResultScope.delay by get<SpawnDelay>()

    private inner class Check : CheckHandler() {
        override fun ResultScope.check(event: EventResultScope): Boolean {
            val iterationMod = (delay.attemptEvery / mobzyConfig.spawnTaskDelay)
                .toInt().coerceAtLeast(1)
            return GlobalSpawnInfo.iterationNumber % iterationMod == 0
        }
    }
}
