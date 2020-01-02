package com.offz.spigot.abyssalcreatures.mobs.flying

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.FlyingMob
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalDiveOnTargetAttack
import net.minecraft.server.v1_15_R1.World

class CorpseWeeper(world: World?) : FlyingMob(world, "Corpse Weeper"), HitBehaviour {
    override fun createPathfinders() {
        super.createPathfinders()
        addPathfinderGoal(2, PathfinderGoalDiveOnTargetAttack(this))
    }

    override val soundAmbient = "entity.corpseweeper.snarl2"
    override val soundDeath = "entity.corpseweeper.snarl2"
    override val soundHurt = "entity.corpseweeper.snarl2"
}