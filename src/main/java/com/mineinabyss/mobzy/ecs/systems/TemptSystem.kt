package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.mobzy.api.helpers.entity.distanceSqrTo
import com.mineinabyss.mobzy.api.helpers.entity.findNearbyPlayer
import com.mineinabyss.mobzy.api.helpers.entity.lookAt
import com.mineinabyss.mobzy.api.nms.aliases.toBukkit
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.moveToEntity
import com.mineinabyss.mobzy.api.pathfindergoals.navigation
import com.mineinabyss.mobzy.ecs.pathfinders.TemptBehavior
import com.mineinabyss.mobzy.ecs.components.minecraft.MobComponent
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityTargetEvent

object TemptSystem : TickingSystem(interval = 40) {
    override fun tick() = Engine.runFor<MobComponent, TemptBehavior> { (mob), temptBehavior ->
        val nearbyPlayer = mob.target as? Player ?: mob.findNearbyPlayer(10.0)?.toBukkit() ?: return@runFor
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