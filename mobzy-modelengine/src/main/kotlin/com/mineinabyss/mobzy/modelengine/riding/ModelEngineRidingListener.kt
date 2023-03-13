package com.mineinabyss.mobzy.modelengine.riding

import com.mineinabyss.geary.helpers.with
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.mobzy.modelengine.intializers.SetModelEngineModel
import com.mineinabyss.mobzy.modelengine.toModelEntity
import com.ticxo.modelengine.api.ModelEngineAPI
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
        val gearyEntity = rightClicked.toGearyOrNull() ?: return
        val modelEntity = rightClicked.toModelEntity() ?: return

        gearyEntity.with { rideable: Rideable, modelengine: SetModelEngineModel ->
            val mount = modelEntity.mountManager

            // If player is not riding this or another entity, mount it
            if (!mount.hasRider(player) && ModelEngineAPI.getMountPair(player.uniqueId) == null) {
                mount.isCanSteer = true
                mount.isCanRide = true

                val controller = ModelEngineAPI.getControllerRegistry().get(rideable.controllerID)
                if (mount.driver == null)
                    mount.setDriver(player, controller)
                else if (rideable.canTakePassengers && mount.passengers.size < rideable.maxPassengerCount)
                    mount.addPassengerToSeat(modelengine.modelId, "p_${mount.passengers.size + 1}", player, controller)
                mount.setCanDamageMount(player.uniqueId, rideable.canDamageMount)
            }
        }
    }

    /** Controlling of entities with [Rideable.requiresItemToSteer] */
    @EventHandler
    fun EntityMoveEvent.onMountControl() {
        val gearyEntity = entity.toGearyOrNull() ?: return
        val mount = entity.toModelEntity()?.mountManager ?: return
        val player = (mount.driver ?: return) as? Player ?: return

        //TODO Make mob move on its own if not holding correct item
        gearyEntity.with { rideable: Rideable ->
            if (rideable.requiresItemToSteer && player.inventory.itemInMainHand != rideable.steerItem?.toItemStack())
                isCancelled = true
        }
    }

    /** Apply remaining damage to driver and passengers of a [Rideable] entity when it dies */
    @EventHandler
    fun EntityDamageEvent.onMountFallDamage() {
        val mount = entity.toModelEntity()?.mountManager ?: return
        val health = (entity as LivingEntity).health

        if (entity.toGearyOrNull() == null) return
        if (cause != EntityDamageEvent.DamageCause.FALL) return
        if (health - finalDamage > 0) return

        if (mount.driver != null && mount.driver is Player) {
            val driver = mount.driver as Player
            driver.damage(damage - health)
            driver.lastDamageCause = this
            driver.noDamageTicks = 0
        }

        if (mount.hasPassengers()) mount.passengers.keys.filterIsInstance<Player>().forEach {
            it.damage(damage - health)
            it.lastDamageCause = this
            it.noDamageTicks = 0
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun EntityDeathEvent.dismountPassengers() {
        val mountHandler = entity.toModelEntity()?.mountManager
        if (mountHandler?.driver != null || mountHandler?.hasPassengers() == true) mountHandler.dismountAll()
    }
}
