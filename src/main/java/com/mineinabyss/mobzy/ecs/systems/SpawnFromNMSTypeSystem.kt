package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.ecs.components.removeComponent
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.forEach
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.geary.minecraft.components.SpawnBukkit
import com.mineinabyss.mobzy.api.nms.aliases.BukkitEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.spawnEntity

class SpawnFromNMSTypeSystem : TickingSystem() {
    override fun tick() =
        Engine.forEach<SpawnBukkit, NMSEntityType<*>>(BukkitEntity::class) { (location), type ->
            val entity = location.spawnEntity(type) ?: return@forEach
            addComponent<BukkitEntity>(entity)
            removeComponent<SpawnBukkit>()
        }
}
