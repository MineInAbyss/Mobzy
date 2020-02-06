package com.offz.spigot.abyssalcreatures.mobs.hostile

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.HostileMob
import net.minecraft.server.v1_15_R1.PathfinderGoalLeapAtTarget
import net.minecraft.server.v1_15_R1.World

class Silkfang(world: World?) : HostileMob(world, "Silkfang"), HitBehaviour {
    override fun createPathfinders() {
        super.createPathfinders()
        addPathfinderGoal(1, PathfinderGoalLeapAtTarget(this, 0.3f))
    }
}