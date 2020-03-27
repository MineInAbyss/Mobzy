package com.mineinabyss.mobzy.pathfinders

import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.toNMS
import net.minecraft.server.v1_15_R1.EntityTypes
import kotlin.random.Random

class PathfinderGoalLookAtPlayerPitchLock(
        override val mob: CustomMob,
        private val targetType: EntityTypes<*>,
        private val radius: Double,
        private val startChance: Float = 0.02f) : MobzyPathfinderGoal() {
    var lookAt: org.bukkit.entity.Entity? = null
    private var length = 0

    override fun shouldExecute(): Boolean {
        if (Random.nextFloat() >= startChance) return false

        if (target != null) {
            lookAt = nmsEntity.goalTarget!!.living
            return true
        }

        lookAt = entity.getNearbyEntities(radius, radius, radius)
                .ifEmpty { return false }
                .filter { other -> other!!.toNMS().entityType === targetType }
                .minBy { mob.distanceTo(it) }
        return true
    }

    override fun shouldKeepExecuting(): Boolean {
        return if (lookAt == null ||
                lookAt!!.isDead ||
                mob.distanceTo(lookAt!!) > (radius * radius)) false
        else length > 0
    }

    override fun init() {
        length = Random.nextInt(40, 80)
    }

    override fun reset() {
        lookAt = null
    }

    override fun execute() {
        if (lookAt == null) return
        mob.lookAtPitchLock(lookAt!!)
        --length
    }
}