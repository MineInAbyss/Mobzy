package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.ecs.accessors.EventResultScope
import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.entities.addPrefab
import com.mineinabyss.geary.ecs.events.handlers.ComponentAddHandler
import com.mineinabyss.geary.ecs.prefab.PrefabManager
import com.mineinabyss.geary.minecraft.toPrefabKey
import com.mineinabyss.idofront.nms.entity.typeNamespacedKey
import com.mineinabyss.idofront.typealiases.BukkitEntity

//TODO Make sure this is the first thing to run when priority support comes
@AutoScan
class AddPrefabFromNMSTypeSystem : GearyListener() {
    val ResultScope.bukkitEntity by get<BukkitEntity>()

    private inner class AddPrefab : ComponentAddHandler() {
        override fun ResultScope.handle(event: EventResultScope) {
            PrefabManager[bukkitEntity.typeNamespacedKey.toPrefabKey()]?.let {
                entity.addPrefab(it)
            }
        }
    }
}
