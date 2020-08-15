package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.mobzy.api.helpers.entity.distanceSqrTo
import com.mineinabyss.mobzy.api.helpers.entity.findNearbyPlayer
import com.mineinabyss.mobzy.api.helpers.entity.lookAt
import com.mineinabyss.mobzy.api.nms.aliases.living
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.moveToEntity
import com.mineinabyss.mobzy.api.pathfindergoals.navigation
import com.mineinabyss.mobzy.ecs.behaviors.TemptBehavior
import com.mineinabyss.mobzy.ecs.components.minecraft.EntityComponent
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityTargetEvent

object TemptSystem : TickingSystem(interval = 40) {
    override fun tick() = Engine.runFor<EntityComponent, TemptBehavior> { (mob), temptBehavior ->
        val nearbyPlayer = mob.target as? Player ?: mob.findNearbyPlayer(10.0)?.living ?: return@runFor
        val equipment = nearbyPlayer.equipment ?: return@runFor
        if (temptBehavior.items.any { it == equipment.itemInMainHand.type || it == equipment.itemInOffHand.type }) {
            if (mob.target != nearbyPlayer)
                mob.toNMS().setGoalTarget(nearbyPlayer.toNMS(), EntityTargetEvent.TargetReason.TEMPT, true)
        } else return@runFor

        val target = mob.target ?: return
        mob.lookAt(target)

        val dist = mob.distanceSqrTo(target)
        if (dist in 1.0..(temptBehavior.range * temptBehavior.range)) mob.navigation.moveToEntity(target, temptBehavior.speed)
    }
}