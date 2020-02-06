package com.offz.spigot.abyssalcreatures.mobs.flying

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.FlyingMob
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalDiveOnTargetAttack
import net.minecraft.server.v1_15_R1.World

class Dosetori(world: World?) : FlyingMob(world, "Dosetori"), HitBehaviour {
    override fun createPathfinders() {
        super.createPathfinders()
        addPathfinderGoal(2, PathfinderGoalDiveOnTargetAttack(
                this,
                startDiveDistance = 8.0
        ))
    }
}