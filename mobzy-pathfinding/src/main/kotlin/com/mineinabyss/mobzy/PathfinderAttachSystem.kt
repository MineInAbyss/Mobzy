package com.mineinabyss.mobzy

import com.mineinabyss.geary.ecs.accessors.EventScope
import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.autoscan.Handler
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.Pathfinders
import com.mineinabyss.mobzy.pathfinding.addPathfinderGoal
import com.mineinabyss.mobzy.pathfinding.addTargetSelector
import org.bukkit.entity.Mob

@AutoScan
class PathfinderAttachSystem : GearyListener() {
    val TargetScope.bukkit by get<BukkitEntity>()
    val TargetScope.pathfinders by get<Pathfinders>()

    init {
        allAdded()
    }

    @Handler
    fun TargetScope.attachPathfinders(event: EventScope) {
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
