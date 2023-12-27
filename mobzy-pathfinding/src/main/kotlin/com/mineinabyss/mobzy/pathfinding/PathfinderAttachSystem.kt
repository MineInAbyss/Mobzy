package com.mineinabyss.mobzy.pathfinding

import com.mineinabyss.geary.annotations.optin.UnsafeAccessors
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.nms.aliases.NMSMob
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.pathfinding.components.Pathfinders
import net.minecraft.world.entity.ai.control.LookControl
import org.bukkit.entity.Mob

@AutoScan
class PathfinderAttachSystem : GearyListener() {
    private val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()
    private val Pointers.pathfinders by get<Pathfinders>().whenSetOnTarget()


    private class NoLookControl(entity: NMSMob) : LookControl(entity) {
        override fun tick() {}
    }

    @OptIn(UnsafeAccessors::class)
    override fun Pointers.handle() {
        val mob = bukkit as? Mob ?: return
        val nmsMob = mob.toNMS()
        val (targets, goals) = pathfinders
        val entity = target.entity

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
}
