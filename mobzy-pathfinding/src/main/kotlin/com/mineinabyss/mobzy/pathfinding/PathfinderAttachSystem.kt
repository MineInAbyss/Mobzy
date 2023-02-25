package com.mineinabyss.mobzy.pathfinding

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.pathfinding.components.Pathfinders
import org.bukkit.entity.Mob

@AutoScan
class PathfinderAttachSystem : GearyListener() {
    private val TargetScope.bukkit by onSet<BukkitEntity>()
    private val TargetScope.pathfinders by onSet<Pathfinders>()

    @Handler
    fun TargetScope.attachPathfinders() {
        val mob = bukkit as? Mob ?: return
        val nmsMob = mob.toNMS()
        val (targets, goals) = pathfinders

        if (pathfinders.override) {
            nmsMob.targetSelector.removeAllGoals { true }
            nmsMob.goalSelector.removeAllGoals { true }
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
