package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.papermc.events.GearyAttemptMinecraftSpawnEvent
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.injection.CustomEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.MobSpawnType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

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
                false
            )?.toBukkit()?.apply {
                addScoreboardTag(CustomEntity.ENTITY_VERSION)
            }
        }
    }
}

