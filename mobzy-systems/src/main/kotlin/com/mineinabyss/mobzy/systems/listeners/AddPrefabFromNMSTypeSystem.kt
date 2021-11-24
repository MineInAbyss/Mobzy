package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.systems.GearyHandlerScope
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.entities.addPrefab
import com.mineinabyss.geary.ecs.events.onComponentAdd
import com.mineinabyss.geary.ecs.prefab.PrefabManager
import com.mineinabyss.geary.minecraft.toPrefabKey
import com.mineinabyss.idofront.nms.entity.typeNamespacedKey
import com.mineinabyss.idofront.typealiases.BukkitEntity

//TODO Make sure this is the first thing to run when priority support comes
class AddPrefabFromNMSTypeSystem : GearyListener() {
    val ResultScope.bukkitEntity by get<BukkitEntity>()

    override fun GearyHandlerScope.register() {
        onComponentAdd {
            PrefabManager[bukkitEntity.typeNamespacedKey.toPrefabKey()]?.let {
                entity.addPrefab(it)
            }
        }
    }
}
