package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.systems.GearyHandlerScope
import com.mineinabyss.geary.ecs.events.CheckEvent
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo

class CheckSpawn(
    val spawnInfo: SpawnInfo
) : CheckEvent()

fun GearyHandlerScope.onCheckSpawn(run: ResultScope.(CheckSpawn) -> Boolean) {
    on<CheckSpawn> { event ->
        if (event.success)
            event.success = run(event)
    }
}
