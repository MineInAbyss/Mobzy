package com.offz.spigot.abyssalcreatures.mobs.flying

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.FlyingMob
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalFlyDamageTarget
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalFlyTowardsTarget
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalIdleFlyAboveGround
import net.minecraft.server.v1_15_R1.EntityHuman
import net.minecraft.server.v1_15_R1.PathfinderGoalFloat
import net.minecraft.server.v1_15_R1.PathfinderGoalNearestAttackableTarget
import net.minecraft.server.v1_15_R1.World

class Kazura(world: World?) : FlyingMob(world, "Kazura"), HitBehaviour {
    override fun createPathfinders() {
        addPathfinderGoal(0, PathfinderGoalFloat(this))
        addPathfinderGoal(1, PathfinderGoalFlyDamageTarget(this))
        addPathfinderGoal(3, PathfinderGoalFlyTowardsTarget(this))
        addPathfinderGoal(7, PathfinderGoalIdleFlyAboveGround(this, 2.0, 16.0))
        addTargetSelector(1, PathfinderGoalNearestAttackableTarget(this, EntityHuman::class.java, true))
    }
}