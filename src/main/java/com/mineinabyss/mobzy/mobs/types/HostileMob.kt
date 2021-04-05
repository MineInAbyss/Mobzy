package com.mineinabyss.mobzy.mobs.types

import com.mieninabyss.mobzy.processor.GenerateFromBase
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.NMSWorld
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.api.pathfindergoals.addTargetSelector
import com.mineinabyss.mobzy.ecs.goals.minecraft.*
import com.mineinabyss.mobzy.ecs.goals.targetselectors.minecraft.TargetNearbyPlayer
import net.minecraft.server.v1_16_R2.EntityMonster

@GenerateFromBase(base = MobBase::class, createFor = [EntityMonster::class])
open class HostileMob(type: NMSEntityType<*>, world: NMSWorld) : MobzyEntityMonster(world, type) {
    override fun createPathfinders() {
        addPathfinderGoal(2, MeleeAttackBehavior(attackSpeed = 1.0, seeThroughWalls = false))
        addPathfinderGoal(3, FloatBehavior())
        addPathfinderGoal(7, LandStrollBehavior())
        addPathfinderGoal(7, LookAtPlayerBehavior(radius = 8.0f))
        addPathfinderGoal(8, RandomLookAroundBehavior())

        addTargetSelector(2, TargetNearbyPlayer())
    }

    //TODO make sure hostile mobs still get removed when difficulty is not peaceful without method here

    init {
        addScoreboardTag("hostileMob")
        entity.removeWhenFarAway = true
        attributeMap
    }
}
