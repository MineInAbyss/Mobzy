package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.helpers.with
import com.mineinabyss.geary.papermc.access.toGearyOrNull
import com.mineinabyss.mobzy.ecs.components.interaction.Rideable
import com.mineinabyss.mobzy.systems.systems.ModelEngineSystem.toModelEntity
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

object RidableListener : Listener {

    /** Ride entities with [Rideable] component on right click. */
    @EventHandler
    fun PlayerInteractEntityEvent.rideOnRightClick() {
        val gearyEntity = rightClicked.toGearyOrNull() ?: return
        val modelEntity = rightClicked.toModelEntity() ?: return

        gearyEntity.with { rideable: Rideable ->
            val mount = modelEntity.mountHandler
            if (player.isSneaking || player.inventory.itemInMainHand.type == Material.LEAD) return

            mount.setSteerable(true)
            mount.setCanCarryPassenger(rideable.canTakePassenger)

            if (!mount.hasDriver()) mount.driver = player
            else mount.addPassenger("p_${mount.passengers.size + 1}", player)

            mount.setCanDamageMount(mount.driver, rideable.driverCanDamageMount)
            mount.passengers["mount"]?.passengers?.forEach {
                mount.setCanDamageMount(it, rideable.passengerCanDamageMount)
            }

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
        val player = (mount.driver ?: return) as? Player ?: return

        //TODO Make mob move on its own if not holding correct item
        gearyEntity.with { rideable: Rideable ->
            if (rideable.requiresItemToSteer && player.inventory.itemInMainHand != rideable.steerItem?.toItemStack())
                isCancelled = true
        }
    }
}
