package com.mineinabyss.mobzy.systems.systems

import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.engine.Archetype
import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.systems.GearyHandlerScope
import com.mineinabyss.geary.ecs.events.onComponentAdd
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
class CopyNBTSystem : GearyListener() {
    private val ResultScope.nbt by get<CopyNBT>()
    private val ResultScope.bukkitEntity by get<BukkitEntity>()

    override fun GearyHandlerScope.register() {
        onComponentAdd {
            bukkitEntity.toNMS().load(nbt.compound)
            entity.decodeComponentsFrom(bukkitEntity.persistentDataContainer)
            entity.remove<CopyNBT>()
        }
    }
}
