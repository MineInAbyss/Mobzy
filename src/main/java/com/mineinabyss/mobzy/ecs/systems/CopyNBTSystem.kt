package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.geary.ecs.components.removeComponent
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.forEach
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.geary.minecraft.store.decodeComponentsFrom
import com.mineinabyss.mobzy.api.nms.aliases.BukkitEntity
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.CopyNBT

/**
 * Reads the nbt off the CopyNBT component and applies the compound to the current entity.
 *
 * This is useful for getting a duplicate version of an old entity.
 *
 * TODO MAKE SURE IT ACTUALLY DOES COPY THE PDC!
 */
class CopyNBTSystem : TickingSystem() {
    override fun tick() = Engine.forEach<CopyNBT, BukkitEntity> { (nbtTag), entity ->
        entity.toNMS().save(nbtTag)
        decodeComponentsFrom(entity.persistentDataContainer)
        removeComponent<CopyNBT>()
    }
}
