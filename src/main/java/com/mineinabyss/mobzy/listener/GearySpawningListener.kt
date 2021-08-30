package com.mineinabyss.mobzy.listener

import com.mineinabyss.geary.ecs.entities.addPrefab
import com.mineinabyss.geary.ecs.prefab.PrefabManager
import com.mineinabyss.geary.minecraft.events.GearyAttemptMinecraftSpawnEvent
import com.mineinabyss.geary.minecraft.events.GearyMinecraftPreLoadEvent
import com.mineinabyss.geary.minecraft.toPrefabKey
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.entity.typeNamespacedKey
import com.mineinabyss.idofront.nms.spawnEntity
import com.mineinabyss.mobzy.mobs.CustomEntity
import org.bukkit.entity.Player
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

    @EventHandler
    fun GearyMinecraftPreLoadEvent.addNMSTypeNameAsPrefab() {
        if (bukkitEntity is Player) return
        PrefabManager[bukkitEntity.typeNamespacedKey.toPrefabKey()]?.let {
            entity.addPrefab(it)
        }
    }
}
