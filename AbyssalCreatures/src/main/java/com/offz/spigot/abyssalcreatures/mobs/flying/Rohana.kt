package com.offz.spigot.abyssalcreatures.mobs.flying

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.FlyingMob
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalFlyDamageTarget
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalFlyTowardsTarget
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalHurtByTarget
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalIdleFlyAboveGround
import net.minecraft.server.v1_15_R1.PathfinderGoalFloat
import net.minecraft.server.v1_15_R1.World

class Rohana(world: World?) : FlyingMob(world, "Rohana"), HitBehaviour {
    override fun createPathfinders() {
        addPathfinderGoal(1, PathfinderGoalFloat(this))
        addPathfinderGoal(1, PathfinderGoalFlyDamageTarget(this))
        addPathfinderGoal(3, PathfinderGoalFlyTowardsTarget(this))
        addPathfinderGoal(7, PathfinderGoalIdleFlyAboveGround(this))
        addPathfinderGoal(1, PathfinderGoalHurtByTarget(this)) //TODO convert to targetSelector
    }
}