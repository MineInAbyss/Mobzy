package com.mineinabyss.mobzy.pathfinders

import com.mineinabyss.idofront.items.editItemMeta
import org.bukkit.entity.LivingEntity

class PathfinderGoalWalkingAnimation(val mob: LivingEntity, private val modelID: Int) : PathfinderGoal() {
    private val model
        get() = mob.equipment!!.helmet!!

    override fun shouldExecute(): Boolean {
        val velocity = mob.velocity
        return model.itemMeta!!.customModelData != modelID + 2 && !(velocity.x in -0.001..0.001 && velocity.z in -0.001..0.001)
    }

    override fun shouldKeepExecuting(): Boolean = shouldExecute()

    override fun init() {
    }

    override fun reset() {
        mob.equipment!!.helmet = model.editItemMeta {
            setCustomModelData(modelID)
        }

    }

    override fun execute() {
        mob.equipment!!.helmet = model.editItemMeta {
            setCustomModelData(modelID + 1)
        }
    }
}