package com.mineinabyss.mobzy.mobs.types

import com.mieninabyss.mobzy.processor.GenerateFromBase
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.aliases.NMSWorld
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.pathfinders.WalkingAnimationGoal
import net.minecraft.server.v1_16_R1.*
import java.io.File

/**
 * Originally based off EntityPig
 */
@GenerateFromBase(base = MobBase::class, createFor = [EntityAnimal::class])
class PassiveMob(type: NMSEntityType<*>, world: NMSWorld) : MobzyEntityAnimal(world, type) {
    override fun createPathfinders() {
        addPathfinderGoal(0, WalkingAnimationGoal(entity, type.model))
        addPathfinderGoal(1, PathfinderGoalFloat(this))
        addPathfinderGoal(2, PathfinderGoalPanic(this, 1.25))
        addPathfinderGoal(3, PathfinderGoalBreed(this, 1.0))
        addPathfinderGoal(5, PathfinderGoalFollowParent(this, 1.1))
        addPathfinderGoal(6, PathfinderGoalRandomStrollLand(this, 1.0))
        addPathfinderGoal(7, PathfinderGoalLookAtPlayer(this, EntityPlayer::class.java, 6.0f))
    }

    override fun createChild(entityageable: EntityAgeable): EntityAgeable? = null

    init {
        createFromBase()
        addScoreboardTag("passiveMob")
        entity.removeWhenFarAway = false
    }
}