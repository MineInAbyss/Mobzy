package com.mineinabyss.mobzy.pathfinders

import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.mobzy.api.pathfindergoals.PathfinderGoal
import com.mineinabyss.mobzy.ecs.components.Model
import org.bukkit.entity.Mob

class WalkingAnimationGoal(
        val mob: Mob,
        private val model: Model
) : PathfinderGoal() {
    private var helmet
        get() = mob.equipment?.helmet
        set(value) {
            mob.equipment?.helmet = value
        }

    //play animation when model is not hit model
    override fun shouldExecute(): Boolean =
            helmet?.itemMeta?.customModelData != model.hitId && mob.velocity.lengthSquared() > 0.01

    override fun shouldKeepExecuting(): Boolean = shouldExecute()

    override fun init() {
    }

    override fun reset() {
        if (helmet?.itemMeta?.customModelData != model.hitId)
            helmet = helmet?.editItemMeta {
                setCustomModelData(model.id)
            }
    }

    override fun execute() {
        helmet = helmet?.editItemMeta {
            setCustomModelData(model.walkId)
        }
    }
}