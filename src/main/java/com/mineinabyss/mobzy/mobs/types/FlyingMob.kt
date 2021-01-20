package com.mineinabyss.mobzy.mobs.types

import com.mieninabyss.mobzy.processor.GenerateFromBase
import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.ecs.components.has
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.aliases.NMSWorld
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.ecs.components.initialization.MobCategory
import com.mineinabyss.mobzy.ecs.goals.minecraft.FloatBehavior
import com.mineinabyss.mobzy.spawning.MobCategories
import net.minecraft.server.v1_16_R2.ControllerMoveFlying
import net.minecraft.server.v1_16_R2.EntityFlying

/**
 * Lots of code taken from the EntityGhast class for flying mobs
 */
@GenerateFromBase(base = MobBase::class, createFor = [EntityFlying::class])
class FlyingMob(type: NMSEntityType<*>, world: NMSWorld) : MobzyEntityFlying(world, type) {
    override fun createPathfinders() {
        addPathfinderGoal(1, FloatBehavior())
    }

    init {
        initMob()
        addScoreboardTag("flyingMob")
        entity.removeWhenFarAway = true
        //TODO movement controller is being wacko with speed limits
        moveController = ControllerMoveFlying(this, 1, false) /*MZControllerMoveFlying(this)*/

        if (!has<MobCategory>()) addComponent(MobCategory(MobCategories.FLYING))
    }
}
