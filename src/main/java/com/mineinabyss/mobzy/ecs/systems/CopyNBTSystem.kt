package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.engine.iteration.QueryResult
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
class CopyNBTSystem : TickingSystem() {
    private val QueryResult.nbt by get<CopyNBT>()
    private val QueryResult.bukkitEntity by get<BukkitEntity>()

    override fun QueryResult.tick() {
        bukkitEntity.toNMS().load(nbt.compound)
        entity.decodeComponentsFrom(bukkitEntity.persistentDataContainer)
        entity.remove<CopyNBT>()
    }
}
