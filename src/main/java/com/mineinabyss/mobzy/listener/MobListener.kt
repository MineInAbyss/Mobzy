package com.mineinabyss.mobzy.listener

import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.geary.minecraft.access.gearyOrNull
import com.mineinabyss.geary.minecraft.access.toBukkit
import com.mineinabyss.geary.minecraft.events.GearyMinecraftSpawnEvent
import com.mineinabyss.idofront.entities.leftClicked
import com.mineinabyss.idofront.entities.rightClicked
import com.mineinabyss.idofront.events.call
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.spawning.spawn
import com.mineinabyss.mobzy.api.isCustomAndRenamed
import com.mineinabyss.mobzy.api.isCustomMob
import com.mineinabyss.mobzy.api.toMobzy
import com.mineinabyss.mobzy.ecs.components.death.DeathLoot
import com.mineinabyss.mobzy.ecs.components.initialization.Equipment
import com.mineinabyss.mobzy.ecs.components.initialization.IncreasedWaterSpeed
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import com.mineinabyss.mobzy.ecs.components.interaction.PreventRiding
import com.mineinabyss.mobzy.ecs.components.interaction.Rideable
import com.mineinabyss.mobzy.mobzy
import com.okkero.skedule.schedule
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerStatisticIncrementEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.world.ChunkLoadEvent
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
        val gearyEntity = gearyOrNull(entity) ?: return
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
        entity.with<IncreasedWaterSpeed> { (level) ->
            mob.equipment?.apply {
                boots = ItemStack(Material.STONE).editItemMeta {
                    isUnbreakable = true
                    addEnchant(Enchantment.DEPTH_STRIDER, level, true)
                }
            }
        }

        //add equipment
        entity.with<Equipment> { equipment ->
            mob.equipment?.apply {
                equipment.helmet?.toItemStack()?.let { helmet = it }
                equipment.chestplate?.toItemStack()?.let { chestplate = it }
                equipment.leggings?.toItemStack()?.let { leggings = it }
                equipment.boots?.toItemStack()?.let { boots = it }
            }
        }

        //create an item based on model ID in head slot if entity will be using itself for the model
        entity.with<Model> { model ->
            if (model.small && mob is Ageable) mob.setBaby()
            //TODO model.giant property which would send packets for giant instead of zombie
            mob.equipment?.helmet = model.modelItemStack
            mob.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1, false, false))
        }
    }

    /** Ride entities with [Rideable] component on right click. */
    @EventHandler
    fun PlayerInteractEntityEvent.rideOnRightClick() {
        val gearyEntity = gearyOrNull(rightClicked) ?: return
        if (gearyEntity.has<Rideable>())
            rightClicked.addPassenger(player)
    }

    /**
     * Remove all old entities, which still contain the original `customMob` tag.
     *
     * Update `customMob2` entities with old damage values for models to the new custom-model-data tag.
     */
    @EventHandler
    fun ChunkLoadEvent.onChunkLoad() {
        for (entity in chunk.entities) {
            if (entity.scoreboardTags.contains("customMob")) {
                entity.remove()
            } else if (entity.scoreboardTags.contains("customMob2") && entity is Mob) {
                geary(entity).get<Model>()?.apply { entity.equipment?.helmet = modelItemStack }
                entity.removeScoreboardTag("customMob2")
                entity.addScoreboardTag("customMob3")
            } else if (entity.isCustomMob && entity.toNMS() !is NPC && !entity.isCustomAndRenamed) {
                (entity as LivingEntity).removeWhenFarAway = true
            }
            //TODO: Do we need to despawn non-monster entities?
        }
    }

    /** The magic method that lets you hit entities in their server side hitboxes. */
    //TODO right click doesn't work in adventure mode, but the alternative is a lot worse to deal with. Decide what to do
    //TODO ignore hits on the spoofed entity
    @EventHandler
    fun PlayerInteractEvent.rayTracedHitBoxInteractions() {
        val player = player
        if (leftClicked || rightClicked) {
            //shoot ray to simulate a left/right click, accounting for server-side custom mob hitboxes
            val trace = player.world.rayTrace(
                player.eyeLocation,
                player.location.direction,
                3.0,
                FluidCollisionMode.ALWAYS,
                true,
                0.0
            ) { entity -> entity != player }

            //if we hit a custom mob, attack or fire an event
            //TODO component for this
            trace?.hitEntity?.toMobzy()?.let { hit ->
                if (leftClicked) {
                    isCancelled = true
                    player.toNMS().attack(hit.nmsEntity)
                } else {
                    PlayerInteractEntityEvent(player, hit.entity).call()
                }
            }
        }
    }

    /** Prevents entities with <PreventRiding> component (NPCs) from getting in boats and other vehicles. */
    @EventHandler
    fun VehicleEnterEvent.onVehicleEnter() {
        val gearyEntity = gearyOrNull(entered) ?: return
        if (gearyEntity.has<PreventRiding>())
            isCancelled = true
    }

    //TODO this is not currently working
    @EventHandler
    fun EntityDeathEvent.setExpOnDeath() {
        val gearyEntity = gearyOrNull(entity) ?: return
        gearyEntity.with<DeathLoot> { deathLoot ->
            deathLoot.expToDrop()?.let { droppedExp = it }

            //TODO only enable running commands when we prevent creative players from spawning entities w/ custom data
//            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command)

            val heldItem = entity.killer?.inventory?.itemInMainHand
            val looting = heldItem?.enchantments?.get(Enchantment.LOOT_BONUS_MOBS) ?: 0
            val fire = (heldItem?.enchantments?.get(Enchantment.FIRE_ASPECT) ?: 0) > 0

            deathLoot.drops.toList().mapNotNull { it.chooseDrop(looting, fire) }.forEach {
                entity.location.spawn<Item> {
                    itemStack = it
                }
            }
        }
    }
}
