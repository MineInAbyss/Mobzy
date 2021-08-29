package com.mineinabyss.mobzy.mobs.types

import com.mieninabyss.mobzy.processor.GenerateFromBase
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.NMSWorld
import com.mineinabyss.idofront.nms.aliases.NMSWorldServer
import net.minecraft.server.level.WorldServer
import net.minecraft.world.entity.EntityAgeable
import net.minecraft.world.entity.animal.EntityAnimal

@GenerateFromBase(base = MobBase::class, createFor = [EntityAnimal::class])
open class PassiveMob(type: NMSEntityType<*>, world: NMSWorld) : MobzyEntityAnimal(world, type) {
    override fun createPathfinders() {
//        addPathfinderGoal(1, PathfinderGoalFloat(this))
//        addPathfinderGoal(2, PathfinderGoalPanic(this, 1.25))
//        addPathfinderGoal(3, PathfinderGoalBreed(this, 1.0))
//        addPathfinderGoal(5, PathfinderGoalFollowParent(this, 1.1))
//        addPathfinderGoal(6, PathfinderGoalRandomStrollLand(this, 1.0))
//        addPathfinderGoal(7, PathfinderGoalLookAtPlayer(this, EntityPlayer::class.java, 6.0f))
    }

    override fun createChild(worldServer: WorldServer, entityAgeable: EntityAgeable): EntityAgeable? = null

    init {
        addScoreboardTag("passiveMob")
        entity.removeWhenFarAway = false
    }
}
