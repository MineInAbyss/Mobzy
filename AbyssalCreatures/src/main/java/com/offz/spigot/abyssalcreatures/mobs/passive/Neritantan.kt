package com.offz.spigot.abyssalcreatures.mobs.passive

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.PassiveMob
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalTemptPitchLock
import net.minecraft.server.v1_15_R1.World

class Neritantan(world: World?) : PassiveMob(world, "Neritantan"), HitBehaviour {
    override fun createPathfinders() {
        super.createPathfinders()
        addPathfinderGoal(4, PathfinderGoalTemptPitchLock(this, staticTemplate.temptItems
                ?: error("Cannot create pathfinder without tempt items")))
    }
}