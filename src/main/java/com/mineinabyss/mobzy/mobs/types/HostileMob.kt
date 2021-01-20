package com.mineinabyss.mobzy.mobs.types

import com.mieninabyss.mobzy.processor.GenerateFromBase
import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.ecs.components.has
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.aliases.NMSWorld
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.api.pathfindergoals.addTargetSelector
import com.mineinabyss.mobzy.ecs.components.initialization.MobCategory
import com.mineinabyss.mobzy.ecs.goals.minecraft.*
import com.mineinabyss.mobzy.ecs.goals.targetselectors.minecraft.TargetNearbyPlayer
import com.mineinabyss.mobzy.spawning.MobCategories
import net.minecraft.server.v1_16_R2.EntityMonster


/**
 * Lots of code taken from EntityZombie
 */
@GenerateFromBase(base = MobBase::class, createFor = [EntityMonster::class])
class HostileMob(type: NMSEntityType<*>, world: NMSWorld) : MobzyEntityMonster(world, type) {
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
        initMob()
        addScoreboardTag("hostileMob")
        entity.removeWhenFarAway = true

        if (!has<MobCategory>()) addComponent(MobCategory(MobCategories.FLYING))
    }
}
