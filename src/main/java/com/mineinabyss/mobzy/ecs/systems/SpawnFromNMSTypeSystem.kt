package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.minecraft.access.BukkitEntityAccess
import com.mineinabyss.geary.minecraft.components.SpawnBukkit
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.spawnEntity
import org.bukkit.event.entity.CreatureSpawnEvent

class SpawnFromNMSTypeSystem : TickingSystem() {
    private val spawn by get<SpawnBukkit>()
    private val type by get<NMSEntityType<*>>()

    override fun GearyEntity.tick() {
        val entity = spawn.at.spawnEntity(type, spawnReason = CreatureSpawnEvent.SpawnReason.CUSTOM) ?: return
        //TODO find a way to set this reference as we create the entity
        BukkitEntityAccess.registerEntity(entity, this)
        set(entity)
        remove<SpawnBukkit>()
    }
}
