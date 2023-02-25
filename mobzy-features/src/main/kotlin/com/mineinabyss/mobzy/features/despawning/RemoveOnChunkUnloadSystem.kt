package com.mineinabyss.mobzy.features.despawning

import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkUnloadEvent

class RemoveOnChunkUnloadSystem : Listener {
    @EventHandler
    fun ChunkUnloadEvent.removeCustomOnChunkUnload() {
        for (entity in chunk.entities) {
            val removeOnUnload = entity.toGeary().get<RemoveOnChunkUnload>() ?: continue
            if (!(removeOnUnload.keepIfRenamed && entity.customName() != null))
                entity.remove()
        }
    }
}
