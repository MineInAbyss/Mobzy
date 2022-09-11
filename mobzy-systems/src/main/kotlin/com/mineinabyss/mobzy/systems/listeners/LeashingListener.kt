package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.mobzy.ecs.components.initialization.ModelEngineComponent
import org.bukkit.event.Listener

object LeashingListener : Listener {
    // ModelEngine 3.0 adds this
    /*// Cancel Pufferfish change for leash-entities
    @EventHandler
    fun PufferFishStateChangeEvent.onLeash() {
        isCancelled = entity.toGeary().has<LeashEntity>()
    }

    */
    /** Handling leashing of entities with [ModelEngineComponent] *//*
    @EventHandler
    fun PlayerLeashEntityEvent.onLeashingMob() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val modelEntity = entity.toModelEntity() ?: return

        // Leash is tied to the ModelEngine BaseEntity.
        // This makes it exist for interaction but also invisible
        // Spawn pufferfish, mount it to entity-bone and set leash-holder
        gearyEntity.with { componentEntity: ModelEngineComponent ->
            if (!componentEntity.leashable) return
            if ((entity as? LivingEntity ?: return).isLeashed) return

            val mount = modelEntity.mountManager
            val pufferFish = player.world.spawnEntity(entity.location, EntityType.PUFFERFISH) as PufferFish
            pufferFish.apply {
                puffState = 0
                setAI(false)
                isInvisible = true
                isInvulnerable = true
                toGeary().setPersisting(LeashEntity())
            }

            mount.setCanCarryPassenger(true)
            mount.addPassenger("leash", pufferFish)
            (entity as LivingEntity).setLeashHolder(player)
            (pufferFish as LivingEntity).setLeashHolder(player)
        }
    }

    */
    /** Handle unleashing of entities with [ModelEngineComponent.leashable] *//*
    @EventHandler
    fun PlayerUnleashEntityEvent.onUnleashMob() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val modelEntity = entity.toModelEntity() ?: return
        modelEntity.
        // Leash is tied to the ModelEngine BaseEntity and rendered via a mounted pufferfish.
        // Since we unleash, we unleash BaseEntity and remove pufferfish
        gearyEntity.with { componentEntity: ModelEngineComponent ->
            if (!componentEntity.leashable) return
            //modelEntity.isInvisible = true
            modelEntity. .removePotionEffect(PotionEffectType.INVISIBILITY)
            modelEntity.mountHandler.getPassengersOnBone("leash")
                .filter { it.toGeary().has<LeashEntity>() }.forEach {
                    (it as LivingEntity).setLeashHolder(null)
                    it.remove()
                }
        }
    }

    */
    /** Handle unleashing of entities with [ModelEngineComponent.leashable] on death *//*
    @EventHandler
    fun EntityDamageEvent.unleashOnDeath() {
        if ((entity as? LivingEntity ?: return).health - damage > 0) return
        val modelEntity = entity.toModelEntity() ?: return

        modelEntity.mountHandler?.getPassengersOnBone("leash")?.filter { it.toGeary().has<LeashEntity>() }?.forEach {
            (it as LivingEntity).setLeashHolder(null)
            it.remove()
        } ?: return
    }*/
}
