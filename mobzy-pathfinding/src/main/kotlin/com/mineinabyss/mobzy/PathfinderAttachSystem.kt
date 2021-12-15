package com.mineinabyss.mobzy

import com.mineinabyss.geary.ecs.accessors.EventResultScope
import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.events.handlers.ComponentAddHandler
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.Pathfinders
import com.mineinabyss.mobzy.pathfinding.addPathfinderGoal
import com.mineinabyss.mobzy.pathfinding.addTargetSelector
import org.bukkit.entity.Mob

@AutoScan
class PathfinderAttachSystem : GearyListener() {
    val ResultScope.bukkit by get<BukkitEntity>()
    val ResultScope.pathfinders by get<Pathfinders>()

    private inner class AttachPathfinders : ComponentAddHandler() {
        override fun ResultScope.handle(event: EventResultScope) {
            val mob = bukkit as? Mob ?: return
            val (targets, goals) = pathfinders

            targets?.forEach { (priority, component) ->
                mob.toNMS().addTargetSelector(priority.toInt(), component)

                entity.set(component)
            }
            goals?.forEach { (priority, component) ->
                mob.toNMS().addPathfinderGoal(priority.toInt(), component)
                entity.set(component)
            }
        }
    }
}
