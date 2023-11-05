package com.mineinabyss.mobzy.modelengine.riding

import com.mineinabyss.geary.helpers.with
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.mobzy.modelengine.getMountManager
import com.mineinabyss.mobzy.modelengine.intializers.SetModelEngineModel
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

class ModelEngineRidingListener : Listener {

    /** Ride entities with [Rideable] component on right click. */
    @EventHandler
    fun PlayerInteractEntityEvent.rideOnRightClick() {
        if (player.isSneaking || player.inventory.itemInMainHand.type == Material.LEAD) return

        rightClicked.toGearyOrNull()?.with { rideable: Rideable, modelengine: SetModelEngineModel ->
            val mountManager = rightClicked.getMountManager() ?: return@with
            if (mountManager.driver == null) mountManager.mountDriver(player, rideable.controller)
        }
    }

    /** Controlling of entities with [Rideable.requiresItemToSteer] */
    @EventHandler
    fun EntityMoveEvent.onMountControl() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        if (!gearyEntity.has<Rideable>()) return
        val mountManager = entity.getMountManager() ?: return
        val player = (mountManager.driver ?: return) as? Player ?: return

        //TODO Make mob move on its own if not holding correct item
        gearyEntity.with { rideable: Rideable ->
            if (player.inventory.itemInMainHand != rideable.steerItem?.toItemStack())
                isCancelled = true
        }
    }

    /** Apply remaining damage to driver and passengers of a [Rideable] entity when it dies */
    @EventHandler
    fun EntityDamageEvent.onMountFallDamage() {
        val mountManager = entity.getMountManager() ?: return
        val health = (entity as? LivingEntity)?.health ?: return

        if (entity.toGearyOrNull() == null) return
        if (cause != EntityDamageEvent.DamageCause.FALL) return
        if (health - finalDamage > 0) return

        if (mountManager.driver != null && mountManager.driver is Player) {
            val driver = mountManager.driver as Player
            driver.damage(damage - health)
            driver.lastDamageCause = this
            driver.noDamageTicks = 0
        }

        /*if (mountManager.hasPassengers()) mountManager.passengers.keys.filterIsInstance<Player>().forEach {
            it.damage(damage - health)
            it.lastDamageCause = this
            it.noDamageTicks = 0
        }*/
    }

    @EventHandler(priority = EventPriority.LOW)
    fun EntityDeathEvent.dismountPassengers() {
        val mountHandler = entity.getMountManager() ?: return
        if (mountHandler.hasRiders()) mountHandler.dismountAll()
    }

    /*@EventHandler(priority = EventPriority.LOWEST)
    fun PlayerQuitEvent.dismountOnQuit() {
        ModelEngineAPI.getMountPairManager().getMountedPair(player.uniqueId)?.mountManager?.let {
            if (it.driver.uniqueId == player.uniqueId) it.removeDriver()
            else it.removePassenger(player)
        }
    }*/
}
