package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.ecs.api.entities.with
import com.mineinabyss.geary.papermc.access.toBukkit
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.geary.papermc.access.toGearyOrNull
import com.mineinabyss.geary.papermc.events.GearyMinecraftSpawnEvent
import com.mineinabyss.idofront.entities.leftClicked
import com.mineinabyss.idofront.entities.rightClicked
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
import com.mineinabyss.mobzy.systems.systems.ModelEngineSystem.toModelEntity
import com.okkero.skedule.schedule
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Ageable
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.player.*
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

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

    // TODO Make pig appearing and dissapearing less visible
    /** Handle leashing of entities with [ModelEngineComponent.leashable] */
    @EventHandler
    fun PlayerLeashEntityEvent.onLeashMob() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val modelEntity = entity.toModelEntity() ?: return

        gearyEntity.with { componentEntity: ModelEngineComponent ->
            if (!componentEntity.leashable) return

            modelEntity.isInvisible = false
            //mobzy.schedule { waitFor(10) }
            modelEntity.entity.addPotionEffect(
                PotionEffect(
                    PotionEffectType.INVISIBILITY,
                    Int.MAX_VALUE,
                    1,
                    false,
                    false
                )
            )
        }
    }

    /** Handle unleashing of entities with [ModelEngineComponent.leashable] */
    @EventHandler
    fun PlayerUnleashEntityEvent.onUnleashMob() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val modelEntity = entity.toModelEntity() ?: return

        // Remove BaseEntity-packet again for hitbox reasons and remove invis effect
        gearyEntity.with { componentEntity: ModelEngineComponent ->
            if (!componentEntity.leashable) return

            modelEntity.isInvisible = true
            modelEntity.entity.removePotionEffect(PotionEffectType.INVISIBILITY)
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

            mount.setSteerable(true)
            mount.setCanCarryPassenger(rideable.canTakePassenger)

            if (!mount.hasDriver()) mount.driver = player
            else mount.addPassenger("p_${mount.passengers.size + 1}", player)

            if (rideable.canTakePassenger && mount.passengers.size < rideable.maxPassengerCount) {
                mount.addPassenger("p_${mount.passengers.size + 1}", player) // Adds passenger to the next seat
            }
        }
    }

    /** Controlling of entities with [Rideable.requiresItemToSteer] */
    @EventHandler
    fun EntityMoveEvent.onMountControl() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val mount = entity.toModelEntity()?.mountHandler ?: return
        val player = (mount.driver ?: return) as Player
        val itemInHand = player.inventory.itemInMainHand

        //TODO Make mob move on its own if not holding correct item
        gearyEntity.with { rideable: Rideable ->
            if (!rideable.isSaddled || rideable.requiresItemToSteer && itemInHand != rideable.steerItem?.toItemStack()) {
                isCancelled = true
            }
        }
    }

    /** Tame entities with [Tamable] component on right click */
    @EventHandler
    fun PlayerInteractEntityEvent.tameMob() {
        val mob = (rightClicked as LivingEntity)
        val gearyEntity = rightClicked.toGearyOrNull() ?: return
        val modelEntity = rightClicked.toModelEntity() ?: return
        val itemInHand = player.inventory.itemInMainHand

        gearyEntity.with { tamable: Tamable, rideable: Rideable ->
            if (tamable.isTamable && !tamable.isTamed && tamable.tameItem?.toItemStack() == itemInHand) {
                val random = Random(1).nextDouble()

                tamable.isTamed = true
                tamable.owner = player.uniqueId
                itemInHand.subtract(1)
                player.spawnParticle(
                    Particle.HEART,
                    rightClicked.location.apply { y += 1.5 },
                    10,
                    random,
                    random,
                    random
                )

                return
            }

            //TODO Heal mob if fed tameItem
            if (tamable.isTamed && (mob.health != mob.maxHealth) && itemInHand == tamable.tameItem?.toItemStack()) {
                if (mob.health + 2 <= mob.maxHealth) mob.health += 2 else mob.health = mob.maxHealth
                player.playSound(mob.location, Sound.ENTITY_HORSE_EAT, 1f, 1f)
                player.spawnParticle(Particle.HEART, rightClicked.location.apply { y += 2 }, 4)

                return
            }

            if (tamable.isTamed && tamable.owner == player.uniqueId && itemInHand.type == Material.NAME_TAG) {
                modelEntity.nametagHandler.setCustomName("nametag", itemInHand.itemMeta.displayName)
                modelEntity.nametagHandler.setCustomNameVisibility("nametag", true)

                return
            }

            // TODO Consider using Guiy to make a fake inventory for armor/saddle/storage
            rideable.isSaddled = false
            if (tamable.isTamed && !rideable.isSaddled && tamable.owner == player.uniqueId && itemInHand.type == Material.SADDLE) {
                val model = gearyEntity.get<ModelEngineComponent>() ?: return
                val saddle = modelEntity.getActiveModel(model.modelId).getPartEntity("saddle")
                //itemInHand.subtract(1)
                rideable.isSaddled = true
                if (!saddle.isVisible) saddle.setItemVisibility(rideable.isSaddled)
                broadcast(saddle.isVisible)
            }
        }
    }

    // TODO Wait for ModelEngine to implement support for this
    @EventHandler
    fun EntityDamageEvent.onMountDamaged() {
//        val gearyEntity = entity.toGearyOrNull() ?: return
//        val modelEntity = entity.toModelEntity() ?: return
//        val mountHandler = modelEntity.mountHandler
//        val mounted = modelEntity.entity
//        val driver = mountHandler?.driver ?: return
//        val vehicle = (driver.vehicle as LivingEntity)
//
//        gearyEntity.with { rideable: Rideable ->
//            vehicle.maxHealth = mounted.maxHealth
//            vehicle.health = mounted.health
//        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun EntityDeathEvent.setExpOnDeath() {
        val gearyEntity = entity.toGearyOrNull() ?: return

        gearyEntity.with { deathLoot: DeathLoot, rideable: Rideable ->
            drops.clear()
            droppedExp = 0

            if (rideable.isSaddled) drops.add(ItemStack(Material.SADDLE))

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

    @EventHandler(priority = EventPriority.LOWEST)
    fun PlayerQuitEvent.onDisconnectOnMount() {
        broadcast("test")
        val mountHandler = player.vehicle.broadcastVal()
        if (player.isInsideVehicle) player.leaveVehicle()
        broadcast(player.isInsideVehicle)
    }
}
