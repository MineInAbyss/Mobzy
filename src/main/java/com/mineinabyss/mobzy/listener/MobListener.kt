package com.mineinabyss.mobzy.listener

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.ecs.components.with
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.idofront.entities.leftClicked
import com.mineinabyss.idofront.entities.rightClicked
import com.mineinabyss.idofront.events.call
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.mobzy.api.isCustomMob
import com.mineinabyss.mobzy.api.isRenamed
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.toMobzy
import com.mineinabyss.mobzy.ecs.components.MobComponent
import com.mineinabyss.mobzy.ecs.components.initialization.Equipment
import com.mineinabyss.mobzy.ecs.components.initialization.IncreasedWaterSpeed
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import com.mineinabyss.mobzy.ecs.components.interaction.Rideable
import com.mineinabyss.mobzy.ecs.events.EntityRightClickEvent
import com.mineinabyss.mobzy.ecs.events.MobSpawnEvent
import com.mineinabyss.mobzy.ecs.has
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.mobzy.registration.MobzyTypes
import com.okkero.skedule.schedule
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.NPC
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerStatisticIncrementEvent
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
     *
     * @param e the event
     */
    @EventHandler
    fun onStatisticIncrement(e: PlayerStatisticIncrementEvent) {
        //if the statistic is entity related and the entity is null, it must be custom, therefore we cancel the event
        if (e.statistic.type == Statistic.Type.ENTITY && e.entityType == null)
            e.isCancelled = true
    }

    /**
     * Switch to the hit model of the entity, then shortly after, back to the normal one to create a hit effect.
     *
     * @param e the event
     */
    @EventHandler(ignoreCancelled = true)
    fun onHit(e: EntityDamageEvent) {
        val mob = e.entity.toMobzy() ?: return
        val model = mob.get<Model>() ?: return
        model.hitId ?: return

        //change the model to its hit version
        val equipment = mob.entity.equipment ?: return
        with(equipment) {
            helmet = helmet?.editItemMeta {
                setCustomModelData(model.hitId)
            }
        }

        //in 5 ticks change the model back to the non hit version
        mobzy.schedule {
            waitFor(7)
            if (!mob.entity.isDead)
                with(equipment) {
                    helmet = helmet?.editItemMeta {
                        setCustomModelData(model.id)
                    }
                }
        }
    }

    /** Check several equipment related components and modify the mob's equipment accordingly when first spawned. */
    @EventHandler
    fun addEquipmentOnMobSpawn(e: MobSpawnEvent) {
        val (entity) = e
        val (mob) = entity.get<MobComponent>() ?: return

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
            mob.equipment?.helmet = model.modelItemStack
            mob.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1, false, false))
        }
    }

    /** Ride entities with [Rideable] component on right click. */
    @EventHandler
    fun rideOnRightClick(event: EntityRightClickEvent) {
        val (player, mob) = event
        if (mob.has<Rideable>())
            mob.addPassenger(player)
    }

    /**
     * Remove all old entities, which still contain the original `customMob` tag.
     *
     * Update `customMob2` entities with old damage values for models to the new custom-model-data tag.
     */
    @EventHandler
    fun onChunkLoad(e: ChunkLoadEvent) {
        for (entity in e.chunk.entities) {
            if (entity.scoreboardTags.contains("customMob")) {
                entity.remove()
            } else if (entity.scoreboardTags.contains("customMob2") && entity is Mob) {
                MobzyTypes.get(entity).get<Model>()?.apply { entity.equipment?.helmet = modelItemStack }
                entity.removeScoreboardTag("customMob2")
                entity.addScoreboardTag("customMob3")
            } else if (entity.isCustomMob && entity.toNMS() !is NPC && !entity.isRenamed) {
                (entity as LivingEntity).removeWhenFarAway = true
            }
        }
    }

    /** Remove entities from ECS when they are removed from Bukkit for any reason (Uses PaperMC event) */
    @EventHandler
    fun onEntityRemove(e: EntityRemoveFromWorldEvent) {
        e.entity.toMobzy()?.let {
            Engine.removeEntity(it)
        }
    }

    /** The magic method that lets you hit entities in their server side hitboxes. */
    //TODO this doesn't work in adventure mode, but the alternative is a lot worse to deal with. Decide what to do
    @EventHandler
    fun onLeftClick(e: PlayerInteractEvent) { // TODO I'd like some way to ignore hits onto the disguised entity. Perhaps use a marker armorstand?
        val player = e.player
        if (e.leftClicked || e.rightClicked) {
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
            trace?.hitEntity?.let { hit ->
                if (hit !is CustomMob || hit !is Mob) return
                if (e.leftClicked) {
                    e.isCancelled = true
                    player.toNMS().attack(hit.toNMS())
                } else {
                    EntityRightClickEvent(player, hit).call()
                }
            }
        }
    }
}