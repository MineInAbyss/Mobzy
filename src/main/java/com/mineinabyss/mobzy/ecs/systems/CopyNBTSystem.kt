package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.query.events.ComponentAddSystem
import com.mineinabyss.geary.minecraft.store.decodeComponentsFrom
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
class CopyNBTSystem : ComponentAddSystem() {
    private val GearyEntity.nbt by get<CopyNBT>()
    private val GearyEntity.bukkitEntity by get<BukkitEntity>()

    override fun GearyEntity.run() {
        bukkitEntity.toNMS().load(nbt.compound)
        decodeComponentsFrom(bukkitEntity.persistentDataContainer)
        remove<CopyNBT>()
    }
}
