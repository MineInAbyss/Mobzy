package com.mineinabyss.mobzy.listener

import com.mineinabyss.geary.minecraft.events.GearyAttemptMinecraftSpawnEvent
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.spawnEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

object GearyAttemptMinecraftSpawnListener : Listener {
    @EventHandler
    fun GearyAttemptMinecraftSpawnEvent.attemptSpawnViaNMS() {
        if (bukkitEntity == null)
            bukkitEntity = prefab.get<NMSEntityType<*>>()?.let { type ->
                location.spawnEntity(type, spawnReason = CreatureSpawnEvent.SpawnReason.CUSTOM)
            }
    }
}
