package com.offz.spigot.abyssalcreatures.mobs.flying

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.FlyingMob
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalDiveOnTargetAttack
import net.minecraft.server.v1_15_R1.World

class Benikuchinawa(world: World?) : FlyingMob(world, "Benikuchinawa"), HitBehaviour {
    override fun createPathfinders() {
        super.createPathfinders()
        addPathfinderGoal(2, PathfinderGoalDiveOnTargetAttack(
                this,
                diveVelocity = -0.03,
                minHeight = 3.0,
                maxHeight = 5.0
        ))
    }

    override val soundAmbient = "entity.llama.ambient"
    override val soundDeath = "entity.llama.death"
    override val soundHurt = "entity.llama.hurt"

}