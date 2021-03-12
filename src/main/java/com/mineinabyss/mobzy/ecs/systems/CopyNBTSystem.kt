package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.minecraft.store.decodeComponentsFrom
import com.mineinabyss.idofront.nms.aliases.BukkitEntity
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.CopyNBT

/**
 * Reads the nbt off the CopyNBT component and applies the compound to the current entity.
 *
 * This is useful for getting a duplicate version of an old entity.
 *
 * TODO MAKE SURE IT ACTUALLY DOES COPY THE PDC!
 */
class CopyNBTSystem : TickingSystem() {
    private val nbt by get<CopyNBT>()
    private val entity by get<BukkitEntity>()

    override fun GearyEntity.tick() {
        entity.toNMS().save(nbt.compound)
        decodeComponentsFrom(entity.persistentDataContainer)
        remove<CopyNBT>()
    }
}
