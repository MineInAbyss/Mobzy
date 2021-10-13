package com.mineinabyss.mobzy.systems.systems

import com.mineinabyss.geary.ecs.components.AddPrefabs
import com.mineinabyss.geary.ecs.entities.addPrefab
import com.mineinabyss.geary.minecraft.events.GearyPrefabLoadEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AddPrefabsListener: Listener {
    @EventHandler
    fun GearyPrefabLoadEvent.onPrefabLoad() {
        val add = entity.get<AddPrefabs>() ?: return
        add.from
            .mapNotNull { it.toEntity()  }
            .forEach { entity.addPrefab(it) }
        entity.remove<AddPrefabs>()
    }
}
