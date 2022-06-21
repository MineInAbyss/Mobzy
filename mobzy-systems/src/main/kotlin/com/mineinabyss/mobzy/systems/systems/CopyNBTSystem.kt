package com.mineinabyss.mobzy.systems.systems

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.papermc.store.decodeComponentsFrom
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.CopyNBT

/**
 * Reads the nbt off the CopyNBT component and applies the compound to the current entity.
 *
 * This is useful for getting a duplicate version of an old entity.
 *
 * TODO MAKE SURE IT ACTUALLY DOES COPY THE PDC!
 */
@AutoScan
class CopyNBTSystem : GearyListener() {
    private val TargetScope.nbt by onSet<CopyNBT>()
    private val TargetScope.bukkitEntity by onSet<BukkitEntity>()

    @Handler
    fun TargetScope.copyNBT() {
        bukkitEntity.toNMS().load(nbt.compound)
        entity.decodeComponentsFrom(bukkitEntity.persistentDataContainer)
        entity.remove<CopyNBT>()
    }
}
