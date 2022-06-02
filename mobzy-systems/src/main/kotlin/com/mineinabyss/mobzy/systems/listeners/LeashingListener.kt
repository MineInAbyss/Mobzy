package com.mineinabyss.mobzy.systems.listeners

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.mineinabyss.geary.helpers.with
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.geary.papermc.access.toGearyOrNull
import com.mineinabyss.mobzy.ecs.components.initialization.ModelEngineComponent
import com.mineinabyss.mobzy.ecs.components.interaction.LeashEntity
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.mobzy.systems.systems.ModelEngineSystem.toModelEntity
import com.mineinabyss.protocolburrito.dsl.ProtocolManagerBurrito
import io.papermc.paper.event.entity.PufferFishStateChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.PufferFish
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.player.PlayerUnleashEntityEvent
import org.bukkit.potion.PotionEffectType

object LeashingListener : Listener {

    // Cancel Pufferfish change for leash-entities
    @EventHandler
    fun PufferFishStateChangeEvent.onLeash() {
        isCancelled = entity.toGeary().has<LeashEntity>()
    }

    /** Handling leashing of entities with [ModelEngineComponent] */
    @EventHandler
    fun PlayerLeashEntityEvent.onLeashingMob() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val modelEntity = entity.toModelEntity() ?: return

        // Leash is tied to the ModelEngine BaseEntity.
        // This makes it exist for interaction but also invisible
        // Spawn pufferfish, mount it to entity-bone and send a leash packet to render leash
        gearyEntity.with { componentEntity: ModelEngineComponent ->
            if (!componentEntity.leashable) return
            if ((entity as? LivingEntity ?: return).isLeashed) return

            val mount = modelEntity.mountHandler
            val pufferFish = player.world.spawnEntity(entity.location, EntityType.PUFFERFISH) as PufferFish
            pufferFish.puffState = 0
            pufferFish.setAI(false)
            pufferFish.isInvisible = true
            pufferFish.isInvulnerable = true
            pufferFish.toGeary().setPersisting(LeashEntity())

            mount.setCanCarryPassenger(true)
            mount.addPassenger("leash", pufferFish)
            (entity as LivingEntity).setLeashHolder(player)
            sendLeashPacket(pufferFish.entityId, player.entityId)
        }
    }

    /** Handle unleashing of entities with [ModelEngineComponent.leashable] */
    @EventHandler
    fun PlayerUnleashEntityEvent.onUnleashMob() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val modelEntity = entity.toModelEntity() ?: return

        // Leash is tied to the ModelEngine BaseEntity and rendered via a mounted pufferfish.
        // Since we unleash, we unleash BaseEntity and remove pufferfish
        gearyEntity.with { componentEntity: ModelEngineComponent ->
            if (!componentEntity.leashable) return
            modelEntity.isInvisible = true
            modelEntity.entity.removePotionEffect(PotionEffectType.INVISIBILITY)
            modelEntity.mountHandler.getPassengersOnBone("leash")
                .filter { it.toGeary().has<LeashEntity>() }.forEach {
                    sendLeashPacket(it.entityId, -1)
                    it.remove()
                }
        }
    }

    /** Handle unleashing of entities with [ModelEngineComponent.leashable] on death */
    @EventHandler
    fun EntityDamageEvent.unleashOnDeath() {
        if ((entity as LivingEntity).health - damage > 0) return
        val modelEntity = entity.toModelEntity() ?: return

        modelEntity.mountHandler?.getPassengersOnBone("leash")?.filter { it.toGeary().has<LeashEntity>() }?.forEach {
            sendLeashPacket(it.entityId, -1)
            it.remove()
        } ?: return
    }

    /** Create and send leash-packet */
    private fun sendLeashPacket(entityId: Int, holderId: Int) {
        val leashPacket = ProtocolManagerBurrito(
            ProtocolLibrary.getProtocolManager(),
            mobzy
        ).createPacket(PacketType.Play.Server.ATTACH_ENTITY)
        leashPacket.integers.write(0, entityId).write(1, holderId)

        Bukkit.getOnlinePlayers().forEach { p ->
            ProtocolManagerBurrito(ProtocolLibrary.getProtocolManager(), mobzy).sendServerPacket(p, leashPacket)
        }
    }
}
