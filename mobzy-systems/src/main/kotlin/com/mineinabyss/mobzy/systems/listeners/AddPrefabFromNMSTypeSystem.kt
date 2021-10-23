package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.ComponentAddSystem
import com.mineinabyss.geary.ecs.entities.addPrefab
import com.mineinabyss.geary.ecs.prefab.PrefabManager
import com.mineinabyss.geary.minecraft.toPrefabKey
import com.mineinabyss.idofront.nms.entity.typeNamespacedKey
import com.mineinabyss.idofront.typealiases.BukkitEntity

//TODO Make sure this is the first thing to run when priority support comes
class AddPrefabFromNMSTypeSystem: ComponentAddSystem() {
    val GearyEntity.bukkitEntity by get<BukkitEntity>()

    override fun GearyEntity.run() {
        PrefabManager[bukkitEntity.typeNamespacedKey.toPrefabKey()]?.let {
            addPrefab(it)
        }
    }
}
