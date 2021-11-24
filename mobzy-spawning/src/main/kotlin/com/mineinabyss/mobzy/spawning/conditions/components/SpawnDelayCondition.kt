package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.systems.GearyHandlerScope
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.mobzy.mobzyConfig
import com.mineinabyss.mobzy.spawning.GlobalSpawnInfo
import com.mineinabyss.mobzy.spawning.conditions.onCheckSpawn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@SerialName("mobzy:check.spawn.delay")
class SpawnDelay(
    @Serializable(with = DurationSerializer::class)
    val attemptEvery: Duration
)

object SpawnDelayCondition : GearyListener() {
    val ResultScope.delay by get<SpawnDelay>()

    override fun GearyHandlerScope.register() {
        onCheckSpawn {
            val iterationMod = (delay.attemptEvery / mobzyConfig.spawnTaskDelay)
                .toInt().coerceAtLeast(1)
            GlobalSpawnInfo.iterationNumber % iterationMod == 0
        }
    }
}
