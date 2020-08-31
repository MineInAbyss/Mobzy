package com.mineinabyss.mobzy.listener

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.idofront.entities.leftClicked
import com.mineinabyss.idofront.entities.rightClicked
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.mobzy.api.isCustomMob
import com.mineinabyss.mobzy.api.isRenamed
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.toMobzy
import com.mineinabyss.mobzy.ecs.components.*
import com.mineinabyss.mobzy.ecs.events.EntityCreatedEvent
import com.mineinabyss.mobzy.ecs.events.EntityRightClickEvent
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.mobzy.registration.MobTypes
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
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
     * We use this method to prevent any entity related statistics if they are from our custom mobs, since it causes
     * server crashes. This is mainly used to prevent entity killing players statistic changes, since player killing
     * entity statistics are already handled by the custom mobs' die() methods (if those are handled here, they seem to
     * still cause errors sometimes).
     *
     * @param e the event
     */
    @EventHandler
    fun onStatisticIncrement(e: PlayerStatisticIncrementEvent) { //if the statistic is entity related and the entity is null, it must be custom, therefore we cancel the event
        if (e.statistic.type == Statistic.Type.ENTITY && e.entityType == null) {
            e.isCancelled = true
        }
    }

    /**
     * Switch to the hit model of the entity, then shortly after, back to the normal one to create a hit effect
     *
     * @param e the event
     */
    @EventHandler(ignoreCancelled = true)
    fun onHit(e: EntityDamageEvent) {
        val mob = e.entity.toMobzy() ?: return
        val model = mob.model ?: return
        model.hitId ?: return

        //change the model to its hit version
        val equipment = mob.entity.equipment ?: return
        equipment.helmet = equipment.helmet?.editItemMeta {
            setCustomModelData(model.hitId)
        }

        //in 5 ticks change the model back to the non hit version
        Bukkit.getScheduler().runTaskLater(mobzy, Runnable {
            if (!mob.entity.isDead)
                equipment.helmet = equipment.helmet?.editItemMeta {
                    setCustomModelData(model.id)
                }
        }, 7)
    }

    @EventHandler
    fun addModelOnEntityCreate(e: EntityCreatedEvent) {
        val entity = Engine.get<MobComponent>(e.id)?.mob ?: return

        Engine.get<IncreasedWaterSpeed>(e.id)?.let { (level) ->
            entity.equipment?.apply {
                boots = ItemStack(Material.STONE).editItemMeta {
                    isUnbreakable = true
                    addEnchant(Enchantment.DEPTH_STRIDER, level, true)
                }
            }
        }
        Engine.get<Equipment>(e.id)?.let { equipment ->
            entity.equipment?.apply {
                equipment.helmet?.toItemStack()?.let { helmet = it }
                equipment.chestplate?.toItemStack()?.let { chestplate = it }
                equipment.leggings?.toItemStack()?.let { leggings = it }
                equipment.boots?.toItemStack()?.let { boots = it }
            }
        }

        val model = Engine.get<Model>(e.id) ?: return

        //create an item based on model ID in head slot if entity will be using itself for the model
        entity.equipment?.helmet = model.modelItemStack
        entity.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1, false, false))
    }

    @EventHandler
    fun rideOnRightClick(event: EntityRightClickEvent) {
        val (player, mob) = event
        if (mob.has<Rideable>())
            mob.addPassenger(player)
    }

    /**
     * Remove all old entities, which still contain the original `customMob` tag
     *
     * Updates `customMob2` entities with old damage values for models to the new custom-model-data tag
     */
    @EventHandler
    fun onChunkLoad(e: ChunkLoadEvent) {
        for (entity in e.chunk.entities) {
            if (entity.scoreboardTags.contains("customMob")) {
                entity.remove()
            } else if (entity.scoreboardTags.contains("customMob2") && entity is Mob) {
                MobTypes[entity].get<Model>()?.apply { entity.equipment?.helmet = modelItemStack }
                entity.removeScoreboardTag("customMob2")
                entity.addScoreboardTag("customMob3")
            } else if (entity.isCustomMob && entity.toNMS() !is NPC && !entity.isRenamed) {
                (entity as LivingEntity).removeWhenFarAway = true
            }
        }
    }

    /*@EventHandler
    fun onDamage(e: EntityDamageEvent){
        if(e.entity.isCustomMob && e.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
            broadcast("damaged!")
    }*/

    /**
     * The magic method that lets you hit entities in their server side hitboxes
     * TODO this doesn't work in adventure mode, but the alternative is a lot worse to deal with. Decide what to do
     */
    @EventHandler
    fun onLeftClick(e: PlayerInteractEvent) { // TODO I'd like some way to ignore hits onto the disguised entity. Perhaps use a marker armorstand?
        val player = e.player
        if (e.leftClicked || e.rightClicked) {
            val trace = player.world.rayTrace(player.eyeLocation, player.location.direction, 3.0, FluidCollisionMode.ALWAYS, true, 0.0) { entity: Entity -> entity != player }
            if (trace != null && trace.hitEntity != null) {
                val hit = trace.hitEntity!!
                if (hit !is Mob) return
                if (e.leftClicked) {
                    e.isCancelled = true
                    player.toNMS().attack(hit.toNMS())
                } else {
                    EntityRightClickEvent(player, hit).callEvent()
                }
            }
        }
    }
}