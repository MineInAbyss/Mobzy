package com.mineinabyss.mobzy

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.ComponentAddSystem
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.Pathfinders
import com.mineinabyss.mobzy.pathfinding.addPathfinderGoal
import com.mineinabyss.mobzy.pathfinding.addTargetSelector
import org.bukkit.entity.Mob

class PathfinderAttachSystem : ComponentAddSystem() {
    val GearyEntity.bukkit by get<BukkitEntity>()
    val GearyEntity.pathfinders by get<Pathfinders>()

    override fun GearyEntity.run() {
        val mob = bukkit as? Mob ?: return
        val (targets, goals) = pathfinders

        targets?.forEach { (priority, component) ->
            mob.toNMS().addTargetSelector(priority.toInt(), component)

            set(component)
        }
        goals?.forEach { (priority, component) ->
            mob.toNMS().addPathfinderGoal(priority.toInt(), component)
            set(component)
        }
    }
}
