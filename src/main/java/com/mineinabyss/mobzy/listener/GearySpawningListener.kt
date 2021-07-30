package com.mineinabyss.mobzy.listener

import com.mineinabyss.geary.ecs.entities.addPrefab
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.ecs.prefab.PrefabManager
import com.mineinabyss.geary.minecraft.events.GearyAttemptMinecraftSpawnEvent
import com.mineinabyss.geary.minecraft.events.GearyMinecraftPreLoadEvent
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.entity.typeName
import com.mineinabyss.idofront.nms.spawnEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

object GearySpawningListener : Listener {
    @EventHandler
    fun GearyAttemptMinecraftSpawnEvent.attemptSpawnViaNMS() {
        if (bukkitEntity == null)
            bukkitEntity = prefab.get<NMSEntityType<*>>()?.let { type ->
                location.spawnEntity(type, spawnReason = CreatureSpawnEvent.SpawnReason.CUSTOM)
            }
    }

    @EventHandler
    fun GearyMinecraftPreLoadEvent.addNMSTypeNameAsPrefab(){
        PrefabManager[PrefabKey("mobzy", bukkitEntity.typeName)]?.let {
            entity.addPrefab(it)
        }
    }
}
