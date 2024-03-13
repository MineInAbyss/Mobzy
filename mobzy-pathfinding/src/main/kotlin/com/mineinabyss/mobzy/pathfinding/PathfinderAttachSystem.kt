package com.mineinabyss.mobzy.pathfinding

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.nms.aliases.NMSMob
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.pathfinding.components.Pathfinders
import net.minecraft.world.entity.ai.control.LookControl
import org.bukkit.entity.Mob

@AutoScan
fun GearyModule.pathfinderSetter() = listener(object : ListenerQuery() {
    val bukkit by get<BukkitEntity>()
    val pathfinders by source.get<Pathfinders>()
}).exec {
    val mob = bukkit as? Mob ?: return@exec
    val nmsMob = mob.toNMS()
    val (targets, goals) = pathfinders

    if (pathfinders.override) {
        nmsMob.targetSelector.removeAllGoals { true }
        nmsMob.goalSelector.removeAllGoals { true }
    }

    if (pathfinders.noLookControl) {
        NMSMob::class.java.declaredFields.first { it.type == LookControl::class.java }.apply {
            isAccessible = true
            set(nmsMob, NoLookControl(nmsMob))
        }
    }

    targets?.forEachIndexed { priority, component ->
        runCatching {
            nmsMob.addTargetSelector(component.preferredPriority ?: priority, component)
            entity.set(component)
        }.onFailure {
            it.printStackTrace()
        }
    }

    goals?.forEachIndexed { priority, component ->
        nmsMob.addPathfinderGoal(component.preferredPriority ?: priority, component)
        entity.set(component)
    }
}


private class NoLookControl(entity: NMSMob) : LookControl(entity) {
    override fun tick() {}
}
