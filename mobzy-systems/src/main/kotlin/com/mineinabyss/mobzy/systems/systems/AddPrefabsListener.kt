package com.mineinabyss.mobzy.systems.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.components.InheritPrefabs
import com.mineinabyss.geary.ecs.entities.addPrefab
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.minecraft.events.GearyPrefabLoadEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AddPrefabsListener : Listener {
    @EventHandler
    fun GearyPrefabLoadEvent.onPrefabLoad() {
        entity.inheritPrefabs()
    }
}

/**
 * Adds prefabs to this entity from an [InheritPrefabs] component. Will make sure parents have their prefabs
 * added from this component before trying to add it
 */
fun GearyEntity.inheritPrefabs(instances: Set<GearyEntity> = setOf()) {
    if (this in instances)
        error("Circular dependency found while loading prefabs for ${get<PrefabKey>()}, chain was: $instances")
    val add = get<InheritPrefabs>() ?: return
    remove<InheritPrefabs>()
    add.from.mapNotNull { it.toEntity() }
        .forEach {
            it.inheritPrefabs(instances + this)
            addPrefab(it)
        }
}
