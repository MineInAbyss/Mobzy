package com.mineinabyss.mobzy.mobs.types

import com.mieninabyss.mobzy.processor.GenerateFromBase
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.aliases.NMSWorld
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.api.pathfindergoals.addTargetSelector
import com.mineinabyss.mobzy.pathfinders.TargetAttackerGoal
import com.mineinabyss.mobzy.pathfinders.flying.FlyDamageTargetGoal
import com.mineinabyss.mobzy.pathfinders.flying.IdleFlyGoal
import net.minecraft.server.v1_16_R1.*

/**
 * Lots of code taken from the EntityGhast class for flying mobs
 */
@GenerateFromBase(base = MobBase::class, createFor = [EntityFlying::class])
class FlyingMob(type: NMSEntityType<*>, world: NMSWorld) : MobzyEntityFlying(world, type) {
    override fun createPathfinders() {
        addPathfinderGoal(1, PathfinderGoalFloat(this))
        addPathfinderGoal(2, FlyDamageTargetGoal(this))
        addPathfinderGoal(5, IdleFlyGoal(this))
        addPathfinderGoal(1, TargetAttackerGoal(this, 100.0)) //TODO should be a target selector instead
        addTargetSelector(2, PathfinderGoalNearestAttackableTarget(this, EntityHuman::class.java, true))
    }

    init {
        initMob()
        addScoreboardTag("flyingMob")
        entity.removeWhenFarAway = true
        moveController = ControllerMoveFlying(this, 20, true) /*MZControllerMoveFlying(this)*/
    }
}