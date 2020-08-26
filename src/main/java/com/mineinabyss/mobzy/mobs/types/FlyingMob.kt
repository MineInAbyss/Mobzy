package com.mineinabyss.mobzy.mobs.types

import com.mieninabyss.mobzy.processor.GenerateFromBase
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.aliases.NMSWorld
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.ecs.goals.mobzy.flying.FlyTowardsTargetGoal
import com.mineinabyss.mobzy.ecs.goals.mobzy.flying.IdleFlyGoal
import com.mineinabyss.mobzy.ecs.goals.targetselectors.TargetAttackerGoal
import net.minecraft.server.v1_16_R2.ControllerMoveFlying
import net.minecraft.server.v1_16_R2.EntityFlying
import net.minecraft.server.v1_16_R2.PathfinderGoalFloat

/**
 * Lots of code taken from the EntityGhast class for flying mobs
 */
@GenerateFromBase(base = MobBase::class, createFor = [EntityFlying::class])
open class FlyingMob(type: NMSEntityType<*>, world: NMSWorld) : MobzyEntityFlying(world, type) {
    override fun createPathfinders() {
        addPathfinderGoal(1, PathfinderGoalFloat(this))
        addPathfinderGoal(2, FlyTowardsTargetGoal(entity))
        addPathfinderGoal(5, IdleFlyGoal(entity))

        addPathfinderGoal(1, TargetAttackerGoal(entity, 100.0)) //TODO should be a target selector instead
    }

    init {
        initMob()
        addScoreboardTag("flyingMob")
        entity.removeWhenFarAway = true
        moveController = ControllerMoveFlying(this, 20, true) /*MZControllerMoveFlying(this)*/
    }
}