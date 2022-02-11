package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.ecs.api.entities.with
import com.mineinabyss.geary.papermc.access.toBukkit
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.geary.papermc.access.toGearyOrNull
import com.mineinabyss.geary.papermc.events.GearyMinecraftSpawnEvent
import com.mineinabyss.idofront.entities.leftClicked
import com.mineinabyss.idofront.entities.rightClicked
import com.mineinabyss.idofront.entities.toPlayer
import com.mineinabyss.idofront.events.call
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.messaging.broadcast
import com.mineinabyss.idofront.messaging.broadcastVal
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.RemoveOnChunkUnload
import com.mineinabyss.mobzy.ecs.components.death.DeathLoot
import com.mineinabyss.mobzy.ecs.components.initialization.Equipment
import com.mineinabyss.mobzy.ecs.components.initialization.IncreasedWaterSpeed
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import com.mineinabyss.mobzy.ecs.components.initialization.ModelEngineComponent
import com.mineinabyss.mobzy.ecs.components.interaction.PreventRiding
import com.mineinabyss.mobzy.ecs.components.interaction.Rideable
import com.mineinabyss.mobzy.ecs.components.interaction.Tamable
import com.mineinabyss.mobzy.injection.extendsCustomClass
import com.mineinabyss.mobzy.injection.isCustomAndRenamed
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.mobzy.modelengine.isModelEngineEntity
import com.mineinabyss.mobzy.systems.systems.ModelEngineSystem.toModelEntity
import com.okkero.skedule.schedule
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Ageable
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerStatisticIncrementEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object MobListener : Listener {
    /**
     * Prevent any entity related statistics if they are from custom mobs, since a NPE on clients which don't know their
     * entity type causes crashes.
     *
     * This is mainly used to prevent entity killing players statistic changes, since player killing entity statistics
     * are already skipped by the custom mobs' die() method.
     */
    @EventHandler
    fun PlayerStatisticIncrementEvent.onStatisticIncrement() {
        //if the statistic is entity related and the entity is null, it must be custom, therefore we cancel the event
        if (statistic.type == Statistic.Type.ENTITY && entityType == null)
            isCancelled = true
    }

    /** Switch to the hit model of the entity, then shortly after, back to the normal one to create a hit effect. */
    @EventHandler(ignoreCancelled = true)
    fun EntityDamageEvent.onHit() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val mob = entity as? LivingEntity ?: return
        val model = gearyEntity.get<Model>() ?: return
        model.hitId ?: return

        //change the model to its hit version
        mob.equipment?.apply {
            helmet = helmet?.editItemMeta {
                setCustomModelData(model.hitId)
            }

            //in a few ticks change the model back to the non hit version
            mobzy.schedule {
                waitFor(7)
                if (!mob.isDead)
                    helmet = helmet?.editItemMeta {
                        setCustomModelData(model.id)
                    }
            }
        }

    }

    /** Check several equipment related components and modify the mob's equipment accordingly when first spawned. */
    @EventHandler
    fun GearyMinecraftSpawnEvent.addEquipmentOnMobSpawn() {
        val mob = entity.toBukkit<Mob>() ?: return

        //add depth strider item on feet to simulate faster water speed TODO do this better
        entity.with { (level): IncreasedWaterSpeed ->
            mob.equipment.apply {
                boots = ItemStack(Material.STONE).editItemMeta {
                    isUnbreakable = true
                    addEnchant(Enchantment.DEPTH_STRIDER, level, true)
                }
            }
        }

        //add equipment
        entity.with { equipment: Equipment ->
            mob.equipment.apply {
                equipment.helmet?.toItemStack()?.let { helmet = it }
                equipment.chestplate?.toItemStack()?.let { chestplate = it }
                equipment.leggings?.toItemStack()?.let { leggings = it }
                equipment.boots?.toItemStack()?.let { boots = it }
            }
        }

        //create an item based on model ID in head slot if entity will be using itself for the model
        entity.with { model: Model ->
            if (model.small && mob is Ageable) mob.setBaby()
            //TODO model.giant property which would send packets for giant instead of zombie
            mob.equipment.helmet = model.modelItemStack
            mob.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1, false, false))
        }
    }

    @EventHandler
    fun ChunkUnloadEvent.removeCustomOnChunkUnload() {
        for (entity in chunk.entities) {
            val removeOnUnload = entity.toGeary().get<RemoveOnChunkUnload>() ?: continue
            val tamed = entity.toGeary().get<Tamable>()?.isTamed ?: continue
            if (!(removeOnUnload.keepIfRenamed && entity.isCustomAndRenamed) || !tamed)
                entity.remove()
        }
    }

    /** The magic method that lets you hit entities in their server side hitboxes. */
    //TODO right click doesn't work in adventure mode, but the alternative is a lot worse to deal with. Decide what to do
    //TODO ignore hits on the spoofed entity
    @EventHandler(priority = EventPriority.LOWEST)
    fun PlayerInteractEvent.rayTracedHitBoxInteractions() {
        if ((leftClicked && hand == EquipmentSlot.HAND || rightClicked)) {
            //shoot ray to simulate a left/right click, accounting for server-side custom mob hitboxes
            val trace = player.world.rayTrace(
                player.eyeLocation,
                player.location.direction,
                3.0,
                FluidCollisionMode.NEVER,
                true,
                0.0
            ) { entity -> entity != player }

            //if we hit a custom mob, attack or fire an event
            //TODO component for this
            trace?.hitEntity?.let { hit ->
                if (!hit.extendsCustomClass) return
                if (leftClicked) {
                    isCancelled = true
                    player.toNMS().attack(hit.toNMS())
                } else {
                    PlayerInteractEntityEvent(player, hit).call()
                }
            }
        }
    }

    /** Prevents entities with <PreventRiding> component (NPCs) from getting in boats and other vehicles. */
    @EventHandler
    fun VehicleEnterEvent.onVehicleEnter() {
        val gearyEntity = entered.toGearyOrNull() ?: return
        if (gearyEntity.has<PreventRiding>())
            isCancelled = true
    }

    /** Ride entities with [Rideable] component on right click. */
    @EventHandler
    fun PlayerInteractEntityEvent.rideOnRightClick() {
        val gearyEntity = rightClicked.toGearyOrNull() ?: return
        val modelEntity = rightClicked.toModelEntity() ?: return

        gearyEntity.with { rideable: Rideable ->
            val mount = modelEntity.mountHandler
            val itemInHand = player.inventory.itemInMainHand

            if (itemInHand.type != Material.AIR) return

            mount.setSteerable(rideable.isSteerable)
            mount.setCanCarryPassenger(rideable.canTakePassenger)

            if (!mount.hasDriver()) mount.driver = player
            if (rideable.canTakePassenger && mount.passengers.size < rideable.maxPassengerCount) {
                mount.addPassenger("passenger${mount.passengers.size + 1}", player) // Adds passenger to the next seat
            }
        }
    }

    //TODO Find a way to render the lead that isnt scuffed
    /** Handling leashing of entities with [ModelEngineComponent] */
    @EventHandler
    fun PlayerLeashEntityEvent.onLeashingMob() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val leashable = gearyEntity.get<ModelEngineComponent>()?.leashable ?: return

        if (!leashable) isCancelled = true
    }

    /** Tame entities with [Tamable] component on right click */
    @EventHandler
    fun PlayerInteractEntityEvent.tameMob() {
        val mob = (rightClicked as LivingEntity)
        val gearyEntity = rightClicked.toGearyOrNull() ?: return
        val modelEntity = rightClicked.toModelEntity() ?: return
        val itemInHand = player.inventory.itemInMainHand

        gearyEntity.with { tamable: Tamable ->
            broadcast(tamable.owner?.toPlayer()?.name)
            tamable.isTamed.broadcastVal("isTamed: ")
            if (tamable.isTamable && !tamable.isTamed && tamable.tameItem?.toItemStack() == itemInHand) {
                tamable.isTamed = true
                tamable.owner = player.uniqueId
                itemInHand.subtract(1)
                player.spawnParticle(Particle.HEART, rightClicked.location.apply { y += 2 }, 10)

                return
            }

            //TODO Heal mob if fed tameItem
            if (tamable.isTamed && (mob.health != mob.maxHealth) && itemInHand == tamable.tameItem?.toItemStack()) {
                mob.health += 2
                player.playSound(mob.location, Sound.ENTITY_HORSE_EAT, 1f, 1f)
                player.spawnParticle(Particle.HEART, rightClicked.location.apply { y += 2 }, 4)

                return
            }

            // TODO Make this work
            if (tamable.isTamed && tamable.owner == player.uniqueId && itemInHand.type == Material.NAME_TAG) {
                rightClicked.customName = itemInHand.itemMeta.displayName
                modelEntity.nametagHandler.setCustomNameVisibility("nametag", true)
                modelEntity.nametagHandler.setCustomName("nametag", rightClicked.customName)
                modelEntity.nametagHandler.getCustomName("nametag").broadcastVal("nametag: ")

                return
            }

            // TODO Make a saddle bone appear if someone "uses" a saddle on their mob
            if (tamable.isTamed && tamable.owner == player.uniqueId && itemInHand.type == Material.SADDLE) {
                broadcast("saddle")
            }
        }
    }

    // TODO Find a way to update the Mount Health Bar with the modelentity/vehicle's
    @EventHandler
    fun EntityDamageEvent.onMountDamaged() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val modelEntity = entity.toModelEntity()
        val mountHandler = modelEntity?.mountHandler
        val driver = mountHandler?.driver
        val vehicle = driver?.vehicle as LivingEntity

        if (entity.isModelEngineEntity) {
            driver.isInsideVehicle.broadcastVal()
            vehicle.health.broadcastVal()
            (entity as LivingEntity).health.broadcastVal()
            vehicle.health = (entity as LivingEntity).health
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    fun EntityDeathEvent.setExpOnDeath() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val mountHandler = entity.toModelEntity()?.mountHandler ?: return
        if (mountHandler.hasDriver() || mountHandler.hasPassengers()) mountHandler.dismountAll()

        gearyEntity.with { deathLoot: DeathLoot ->
            drops.clear()
            droppedExp = 0

            if (entity.lastDamageCause?.cause !in deathLoot.ignoredCauses) {
                deathLoot.expToDrop()?.let { droppedExp = it }
                val heldItem = entity.killer?.inventory?.itemInMainHand
                val looting = heldItem?.enchantments?.get(Enchantment.LOOT_BONUS_MOBS) ?: 0
                drops.addAll(deathLoot.drops.mapNotNull { it.chooseDrop(looting, entity.fireTicks > 0) })

                //TODO only enable running commands when we prevent creative players from spawning entities w/ custom data
//            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command)
            }
        }
    }
}
