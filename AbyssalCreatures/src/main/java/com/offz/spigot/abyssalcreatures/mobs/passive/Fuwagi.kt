package com.offz.spigot.abyssalcreatures.mobs.passive

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.PassiveMob
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalWalkingAnimation
import net.minecraft.server.v1_15_R1.EntityAgeable
import net.minecraft.server.v1_15_R1.EntityHuman
import net.minecraft.server.v1_15_R1.PathfinderGoalAvoidTarget
import net.minecraft.server.v1_15_R1.World

class Fuwagi(world: World?) : PassiveMob(world, "Fuwagi"), HitBehaviour {
    override fun createPathfinders() {
        super.createPathfinders()
        registerPathfinderGoal(0, PathfinderGoalWalkingAnimation(living, staticTemplate.modelID))
        registerPathfinderGoal(1, PathfinderGoalAvoidTarget(this, EntityHuman::class.java, 8.0f, 1.0, 1.0))
        //        goalSelector.a(4, new PathfinderGoalTemptPitchLock(this, 1.2D, false, getStaticBuilder().getTemptItems()));
    }

    override val soundHurt: String? = "entity.rabbit.attack"
    override val soundAmbient: String? = "entity.rabbit.ambient"
    override val soundStep: String? = "entity.rabbit.jump"
    override val soundDeath: String? = "entity.rabbit.death"

    override fun createChild(entityageable: EntityAgeable): PassiveMob? {
        return Fuwagi(this.world)
    }
}