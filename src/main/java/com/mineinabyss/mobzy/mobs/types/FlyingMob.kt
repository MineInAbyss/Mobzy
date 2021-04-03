package com.mineinabyss.mobzy.mobs.types

import com.mieninabyss.mobzy.processor.GenerateFromBase
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.NMSWorld
import net.minecraft.server.v1_16_R2.ControllerMoveFlying
import net.minecraft.server.v1_16_R2.EntityFlying

@GenerateFromBase(base = MobBase::class, createFor = [EntityFlying::class])
open class FlyingMob(type: NMSEntityType<*>, world: NMSWorld) : MobzyEntityFlying(world, type) {
    override fun createPathfinders() {
//        addPathfinderGoal(1, FloatBehavior())
    }

    init {
        addScoreboardTag("flyingMob")
        entity.removeWhenFarAway = true
        //TODO movement controller is being wacko with speed limits
        moveController = ControllerMoveFlying(this, 1, false) /*MZControllerMoveFlying(this)*/
    }
}
