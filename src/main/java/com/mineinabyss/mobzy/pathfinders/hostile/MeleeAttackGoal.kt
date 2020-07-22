package com.mineinabyss.mobzy.pathfinders.hostile

import net.minecraft.server.v1_16_R1.EntityCreature
import net.minecraft.server.v1_16_R1.EntityLiving
import net.minecraft.server.v1_16_R1.PathfinderGoalMeleeAttack

class MeleeAttackGoal(
        val mob: EntityCreature,
        val attackSpeed: Double,
        val seeThroughWalls: Boolean,
        private val range: Double? = null
) : PathfinderGoalMeleeAttack(mob, attackSpeed, seeThroughWalls) {
    override fun a(target: EntityLiving): Double = range ?: mob.bukkitEntity.width * 2 + target.bukkitEntity.width * 2
}