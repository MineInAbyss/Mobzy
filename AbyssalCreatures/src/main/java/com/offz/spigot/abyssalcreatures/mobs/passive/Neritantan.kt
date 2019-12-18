package com.offz.spigot.abyssalcreatures.mobs.passive

import com.offz.spigot.mobzy.debug
import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.PassiveMob
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalWalkingAnimation
import net.minecraft.server.v1_15_R1.World

class Neritantan(world: World?) : PassiveMob(world, "Neritantan"), HitBehaviour {
    override fun createPathfinders() {
        super.createPathfinders()
        debug("I am being created!")
        registerPathfinderGoal(0, PathfinderGoalWalkingAnimation(this.living, staticTemplate.modelID))
//        goalSelector.a(0, )
//        goalSelector.a(4, PathfinderGoalTemptPitchLock(this, 1.2, false, staticTemplate.temptItems));
    }

    /*override fun createChild(entityageable: EntityAgeable): PassiveMob! {
        return Neritantan(world)
    }*/
}