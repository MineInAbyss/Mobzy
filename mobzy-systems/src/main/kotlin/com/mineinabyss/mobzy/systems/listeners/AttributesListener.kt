package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.api.annotations.Handler
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.api.systems.provideDelegate
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import net.minecraft.world.entity.EntityDimensions

//TODO Make sure this is the first thing to run when priority support comes
@AutoScan
class AddPrefabFromNMSTypeSystem : GearyListener() {
    val TargetScope.bukkitEntity by added<BukkitEntity>()
    val TargetScope.attributes by added<MobAttributes>()

    @Handler
    fun TargetScope.addPrefab() {
        val dimensions = NMSEntity::class.java.declaredFields.first { it.type == EntityDimensions::class.java }
        val nms = bukkitEntity.toNMS()
        dimensions.isAccessible = true
        val fixed = (dimensions.get(nms) as EntityDimensions).fixed
        dimensions.set(nms, EntityDimensions(attributes.width, attributes.height, fixed))
    }
}
