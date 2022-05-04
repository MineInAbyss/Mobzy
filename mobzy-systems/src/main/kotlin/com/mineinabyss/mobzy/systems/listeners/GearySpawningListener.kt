package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.papermc.events.GearyAttemptMinecraftSpawnEvent
import com.mineinabyss.geary.papermc.events.GearyMinecraftSpawnEvent
import com.mineinabyss.geary.prefabs.helpers.addPrefab
import com.mineinabyss.idofront.events.call
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.injection.helpers.toGeary
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobSpawnType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

object GearySpawningListener : Listener {
    @EventHandler
    fun GearyAttemptMinecraftSpawnEvent.attemptSpawnViaNMS() {
        if (bukkitEntity != null) return
        val world = location.world.toNMS()
        val spawned = prefab.get<NMSEntityType<*>>()?.create(world) ?: return
        spawned.moveTo(location.x, location.y, location.z)

        spawned.toGeary().apply {
            addPrefab(prefab)
            set(spawned.toBukkit())
            GearyMinecraftSpawnEvent(this).call()
        }
        if (spawned is Mob) {
            spawned.finalizeSpawn(
                world,
                world.getCurrentDifficultyAt(spawned.blockPosition()),
                MobSpawnType.COMMAND,
                null,
                null
            )
        }
        world.addFreshEntityWithPassengers(spawned, CreatureSpawnEvent.SpawnReason.COMMAND)
        bukkitEntity = spawned.toBukkit()
    }
}

