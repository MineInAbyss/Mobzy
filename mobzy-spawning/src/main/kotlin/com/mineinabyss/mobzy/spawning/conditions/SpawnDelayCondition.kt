package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.idofront.time.TimeSpan
import com.mineinabyss.mobzy.mobzyConfig
import com.mineinabyss.mobzy.spawning.GlobalSpawnInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:spawn.delay")
class SpawnDelayCondition(
    private val attemptEvery: TimeSpan
) : GearyCondition() {
    private val iterationMod: Int = (attemptEvery.inMillis / mobzyConfig.spawnTaskDelay.inMillis)
        .toInt().coerceAtLeast(1)

    override fun GearyEntity.check(): Boolean =
        GlobalSpawnInfo.iterationNumber % iterationMod == 0
}
