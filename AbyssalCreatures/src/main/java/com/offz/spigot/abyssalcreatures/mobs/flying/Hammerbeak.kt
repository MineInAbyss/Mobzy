package com.offz.spigot.abyssalcreatures.mobs.flying

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.FlyingMob
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalDiveOnTargetAttack
import net.minecraft.server.v1_15_R1.World

class Hammerbeak(world: World?) : FlyingMob(world, "Hammerbeak"), HitBehaviour {
    override fun createPathfinders() {
        super.createPathfinders()
        addPathfinderGoal(2, PathfinderGoalDiveOnTargetAttack(
                this,
                diveVelocity = -0.6,
                minHeight = 8.0,
                maxHeight = 12.0
        ))
    }
}