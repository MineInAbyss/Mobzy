package com.mineinabyss.mobzy.features.copynbt

import com.mineinabyss.geary.annotations.optin.UnsafeAccessors
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.papermc.datastore.loadComponentsFrom
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity

/**
 * Reads the nbt off the CopyNBT component and applies the compound to the current entity.
 *
 * This is useful for getting a duplicate version of an old entity.
 */
@AutoScan
class CopyNBTSystem : GearyListener() {
    private val Pointers.nbt by get<CopyNBT>().whenSetOnTarget()
    private val Pointers.bukkitEntity by get<BukkitEntity>().whenSetOnTarget()

    @OptIn(UnsafeAccessors::class)
    override fun Pointers.handle() {
        bukkitEntity.toNMS().load(nbt.compound)
        target.entity.loadComponentsFrom(bukkitEntity.persistentDataContainer)
        target.entity.remove<CopyNBT>()
    }
}
