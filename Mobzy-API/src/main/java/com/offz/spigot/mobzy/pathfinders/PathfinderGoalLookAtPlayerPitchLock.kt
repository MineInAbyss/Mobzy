package com.offz.spigot.mobzy.pathfinders

import com.offz.spigot.mobzy.mobs.CustomMob
import com.offz.spigot.mobzy.toNMS
import net.minecraft.server.v1_15_R1.EntityInsentient
import net.minecraft.server.v1_15_R1.EntityTypes
import org.bukkit.entity.LivingEntity
import kotlin.random.Random

class PathfinderGoalLookAtPlayerPitchLock(
        private var mob: CustomMob,
        private val targetType: EntityTypes<*>,
        private val radius: Double,
        private val startChance: Float = 0.02f) : com.offz.spigot.mobzy.pathfinders.PathfinderGoal() {
    var entity: LivingEntity = mob.entity.bukkitEntity as LivingEntity
    var nmsEntity: EntityInsentient = mob.entity as EntityInsentient
    var lookAt: org.bukkit.entity.Entity? = null
    private var length = 0

    override fun shouldExecute(): Boolean {
        if (Random.nextFloat() >= startChance) return false
        if (nmsEntity.goalTarget != null) {
            lookAt = nmsEntity.goalTarget!!.living
            return true
        }

        lookAt = entity.getNearbyEntities(radius, radius, radius)
                .ifEmpty { return false }
                .filter { other -> other!!.toNMS().entityType === targetType }
                .minBy { mob.distanceToEntity(it) }
//        nmsEntity.goalTarget = lookAt!!.toNMS() as EntityLiving
        return true
    }

    override fun shouldTerminate(): Boolean {
        if(lookAt == null) return true
        return if (lookAt!!.isDead ||
                mob.distanceToEntity(lookAt!!) > (radius * radius)) false
        else length > 0
    }

    override fun init() {
        length = Random.nextInt(40, 80)
    }

    override fun reset() {
        lookAt = null
    }

    override fun execute() {
        if(lookAt == null) return
        mob.lookAtPitchLock(lookAt!!)
        --length
    }
}