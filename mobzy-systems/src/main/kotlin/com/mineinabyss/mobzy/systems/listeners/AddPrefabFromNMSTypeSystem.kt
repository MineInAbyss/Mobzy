package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.annotations.Handler
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.papermc.toPrefabKey
import com.mineinabyss.geary.prefabs.PrefabManager
import com.mineinabyss.geary.prefabs.PrefabManagerScope
import com.mineinabyss.geary.prefabs.helpers.addPrefab
import com.mineinabyss.idofront.nms.entity.typeNamespacedKey
import com.mineinabyss.idofront.typealiases.BukkitEntity
import org.koin.core.component.inject

//TODO Make sure this is the first thing to run when priority support comes
@AutoScan
class AddPrefabFromNMSTypeSystem : GearyListener(), PrefabManagerScope {
    override val prefabManager: PrefabManager by inject()
    val TargetScope.bukkitEntity by added<BukkitEntity>()

    @Handler
    fun TargetScope.addPrefab() {
        prefabManager[bukkitEntity.typeNamespacedKey.toPrefabKey()]?.let {
            entity.addPrefab(it)
        }
    }
}
