package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.autoscan.Handler
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.minecraft.toPrefabKey
import com.mineinabyss.geary.prefabs.PrefabManager
import com.mineinabyss.geary.prefabs.helpers.addPrefab
import com.mineinabyss.idofront.nms.entity.typeNamespacedKey
import com.mineinabyss.idofront.typealiases.BukkitEntity

//TODO Make sure this is the first thing to run when priority support comes
@AutoScan
class AddPrefabFromNMSTypeSystem : GearyListener() {
    val TargetScope.bukkitEntity by get<BukkitEntity>()

    init {
        allAdded()
    }

    @Handler
    fun TargetScope.addPrefab() {
        PrefabManager[bukkitEntity.typeNamespacedKey.toPrefabKey()]?.let {
            entity.addPrefab(it)
        }
    }
}
