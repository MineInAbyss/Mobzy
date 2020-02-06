package com.offz.spigot.abyssalcreatures.mobs.flying

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.FlyingMob
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalDiveOnTargetAttack
import net.minecraft.server.v1_15_R1.World

class Madokajack(world: World?) : FlyingMob(world, "Madokajack"), HitBehaviour {
    override fun createPathfinders() {
        super.createPathfinders()
        addPathfinderGoal(2, PathfinderGoalDiveOnTargetAttack(
                this,
                minHeight = 6.0,
                maxHeight = 10.0,
                startDiveDistance = 14.0,
                bashVelMultiplier = 1.0
        ))
    }
}