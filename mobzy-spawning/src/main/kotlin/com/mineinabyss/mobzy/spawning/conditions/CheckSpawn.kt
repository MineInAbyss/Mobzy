package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.accessors.EventResultScope
import com.mineinabyss.geary.ecs.api.systems.GearyHandlerScope
import com.mineinabyss.geary.ecs.events.onCheck
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo

fun GearyHandlerScope.onCheckSpawn(predicate: EventResultScope.(SpawnInfo) -> Boolean) {
    onCheck { check ->
        val spawn = event.get<SpawnInfo>() ?: return@onCheck false
        predicate(spawn)
    }
}
