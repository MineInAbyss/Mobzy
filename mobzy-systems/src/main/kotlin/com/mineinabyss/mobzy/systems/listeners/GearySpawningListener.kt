package com.mineinabyss.mobzy.systems.listeners

import com.github.shynixn.mccoroutine.bukkit.launch
import com.mineinabyss.geary.papermc.events.GearyAttemptMinecraftSpawnEvent
import com.mineinabyss.geary.papermc.events.GearyMinecraftSpawnEvent
import com.mineinabyss.geary.prefabs.helpers.addPrefab
import com.mineinabyss.idofront.events.call
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.OnSpawn
import com.mineinabyss.mobzy.injection.helpers.toGeary
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.mobzy.modelengine.playAnimation
import kotlinx.coroutines.delay
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobSpawnType
import org.bukkit.entity.LivingEntity
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

        val onSpawn = spawned.toGeary().get<OnSpawn>() ?: return
        if (bukkitEntity !is LivingEntity) return

        bukkitEntity?.playAnimation(onSpawn.animationName, 0, 0, 1.0)
        mobzy.launch {
            // Remove MobAi so it doesnt move whilst spawn animation is being played
            (bukkitEntity as LivingEntity).setAI(false)
            delay(onSpawn.animationLength)
            (bukkitEntity as LivingEntity).setAI(true)
        }
    }
}

