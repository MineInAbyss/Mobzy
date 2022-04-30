package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.papermc.events.GearyAttemptMinecraftSpawnEvent
import com.mineinabyss.geary.papermc.events.GearyMinecraftSpawnEvent
import com.mineinabyss.geary.prefabs.helpers.addPrefab
import com.mineinabyss.idofront.events.call
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.injection.helpers.toGeary
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.MobSpawnType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

object GearySpawningListener : Listener {
    @EventHandler
    fun GearyAttemptMinecraftSpawnEvent.attemptSpawnViaNMS() {
        if (bukkitEntity == null) bukkitEntity = prefab.get<NMSEntityType<*>>()?.let { type ->
            type.spawn(
                location.world.toNMS(),
                null,
                null,
                null,
                BlockPos(location.x, location.y, location.z),
                MobSpawnType.COMMAND,
                true,
                false,
                CreatureSpawnEvent.SpawnReason.COMMAND
            ) {
                it.toGeary().apply {
                    addPrefab(prefab)
                    set<BukkitEntity>(it.toBukkit())
                    GearyMinecraftSpawnEvent(this).call()
                }
            }?.toBukkit()
        }

//        type.create(
//            world
//        )?.apply {
//            toGeary().apply {
//                addPrefab(prefab)
//                set<BukkitEntity>(toBukkit())
//                GearyMinecraftSpawnEvent(this).call()
//            }
//        }?.toBukkit()
    }
}

