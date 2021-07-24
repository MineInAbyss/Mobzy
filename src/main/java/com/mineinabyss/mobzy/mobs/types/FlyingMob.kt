package com.mineinabyss.mobzy.mobs.types

import com.mieninabyss.mobzy.processor.GenerateFromBase
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.NMSWorld
import net.minecraft.world.entity.EntityFlying
import net.minecraft.world.entity.ai.control.ControllerMoveFlying

@GenerateFromBase(base = MobBase::class, createFor = [EntityFlying::class])
open class FlyingMob(type: NMSEntityType<*>, world: NMSWorld) : MobzyEntityFlying(world, type) {
    override fun createPathfinders() {
//        addPathfinderGoal(1, FloatBehavior())
    }

    init {
        addScoreboardTag("flyingMob")
        entity.removeWhenFarAway = true
        //TODO movement controller is being wacko with speed limits
        bM /* ControllerMove */ = ControllerMoveFlying(this, 1, false) /*MZControllerMoveFlying(this)*/
    }
}
