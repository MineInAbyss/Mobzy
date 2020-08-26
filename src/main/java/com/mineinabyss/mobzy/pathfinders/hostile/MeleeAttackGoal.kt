package com.mineinabyss.mobzy.pathfinders.hostile

import com.mineinabyss.mobzy.api.helpers.entity.reachDistance
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import net.minecraft.server.v1_16_R2.EntityCreature
import net.minecraft.server.v1_16_R2.EntityLiving
import net.minecraft.server.v1_16_R2.PathfinderGoalMeleeAttack
import org.bukkit.entity.Creature

class MeleeAttackGoal(
        val entity: Creature,
        val attackSpeed: Double,
        val seeThroughWalls: Boolean,
        private val range: Double? = null
) : PathfinderGoalMeleeAttack(entity.toNMS<EntityCreature>(), attackSpeed, seeThroughWalls) {
    override fun a(target: EntityLiving): Double = range ?: entity.reachDistance(target.bukkitEntity)
}