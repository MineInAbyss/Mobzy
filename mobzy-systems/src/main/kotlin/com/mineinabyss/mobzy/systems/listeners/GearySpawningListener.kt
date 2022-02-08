package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.papermc.events.GearyAttemptMinecraftSpawnEvent
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.spawnEntity
import com.mineinabyss.mobzy.injection.CustomEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

object GearySpawningListener : Listener {
    @EventHandler
    fun GearyAttemptMinecraftSpawnEvent.attemptSpawnViaNMS() {
        if (bukkitEntity == null)
            bukkitEntity = prefab.get<NMSEntityType<*>>()?.let { type ->
                location.spawnEntity(type, spawnReason = CreatureSpawnEvent.SpawnReason.CUSTOM)?.apply {
                    //TODO convert to component?
                    addScoreboardTag(CustomEntity.ENTITY_VERSION)
                }
            }
    }
}

