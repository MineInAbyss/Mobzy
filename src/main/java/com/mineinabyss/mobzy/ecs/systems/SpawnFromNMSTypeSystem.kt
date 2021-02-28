package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.api.systems.accessor
import com.mineinabyss.geary.minecraft.components.SpawnBukkit
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.spawnEntity

class SpawnFromNMSTypeSystem : TickingSystem() {
    private val spawn by accessor<SpawnBukkit>()
    private val type by accessor<NMSEntityType<*>>()

    override fun GearyEntity.tick() {
        val entity = spawn.at.spawnEntity(type) ?: return
        set(entity)
        remove<SpawnBukkit>()
    }
}
