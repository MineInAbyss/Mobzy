package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.helpers.with
import com.mineinabyss.geary.papermc.access.toGearyOrNull
import com.mineinabyss.mobzy.ecs.components.interaction.Rideable
import com.mineinabyss.mobzy.systems.systems.ModelEngineSystem.toModelEntity
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

object RidableListener : Listener {

    /** Ride entities with [Rideable] component on right click. */
    @EventHandler
    fun PlayerInteractEntityEvent.rideOnRightClick() {
        val gearyEntity = rightClicked.toGearyOrNull() ?: return
        val modelEntity = rightClicked.toModelEntity() ?: return

        gearyEntity.with { rideable: Rideable ->
            val mount = modelEntity.mountManager
            if (player.isSneaking || player.inventory.itemInMainHand.type == Material.LEAD) return

            mount.isCanSteer = true

            if (mount.driver != null) mount.setDriver(player, mount.driverController)
            else mount.addPassengerToSeat("something", "p_${mount.passengers.size + 1}", player, null)

            mount.setCanDamageMount(mount.driver.uniqueId, false)
            /*mount.passengers["mount"]?.passengers?.forEach {
                mount.setCanDamageMount(it, rideable.passengerCanDamageMount)
            }*/

            if (rideable.canTakePassenger && mount.passengers.size < rideable.maxPassengerCount) {
                mount.addPassengerToSeat(
                    "something",
                    "p_${mount.passengers.size + 1}",
                    player,
                    null
                ) // Adds passenger to the next seat
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
        /*mount.passengers["mount"]?.passengers?.filterIsInstance<Player>()?.forEach {
            it.damage(damage - health)
            it.lastDamageCause = this
            it.noDamageTicks = 0
        }*/
    }
}
