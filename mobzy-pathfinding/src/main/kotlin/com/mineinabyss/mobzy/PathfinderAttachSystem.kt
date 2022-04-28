package com.mineinabyss.mobzy

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.Pathfinders
import com.mineinabyss.mobzy.pathfinding.addPathfinderGoal
import com.mineinabyss.mobzy.pathfinding.addTargetSelector
import org.bukkit.entity.Creature

@AutoScan
class PathfinderAttachSystem : GearyListener() {
    val TargetScope.bukkit by added<BukkitEntity>()
    val TargetScope.pathfinders by added<Pathfinders>()

    @Handler
    fun TargetScope.attachPathfinders() {
        val mob = bukkit as? Creature ?: return
        val nmsMob = mob.toNMS()
        val (targets, goals) = pathfinders

        if(pathfinders.override) {
            nmsMob.targetSelector.removeAllGoals()
            nmsMob.goalSelector.removeAllGoals()
        }

        targets?.forEach { (priority, component) ->
            nmsMob.addTargetSelector(priority.toInt(), component)
            entity.set(component)
        }

        goals?.forEach { (priority, component) ->
            nmsMob.addPathfinderGoal(priority.toInt(), component)
            entity.set(component)
        }
    }
}
