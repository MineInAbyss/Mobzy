package com.mineinabyss.mobzy.features.copynbt

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.papermc.datastore.loadComponentsFrom
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity

/**
 * Reads the nbt off the CopyNBT component and applies the compound to the current entity.
 *
 * This is useful for getting a duplicate version of an old entity.
 */
@AutoScan
fun GearyModule.createCopyNBTSystem() = listener(object : ListenerQuery() {
    val bukkitEntity by get<BukkitEntity>()
    val nbt by get<CopyNBT>()
    override fun ensure() = event.anySet(::bukkitEntity, ::nbt)
}).exec {
    bukkitEntity.toNMS().load(nbt.compound)
    entity.loadComponentsFrom(bukkitEntity.persistentDataContainer)
    entity.remove<CopyNBT>()
}
