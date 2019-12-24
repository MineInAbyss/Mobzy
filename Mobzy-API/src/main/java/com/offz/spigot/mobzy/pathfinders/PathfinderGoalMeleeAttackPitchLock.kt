package com.offz.spigot.mobzy.pathfinders

import com.offz.spigot.mobzy.mobs.CustomMob
import net.minecraft.server.v1_15_R1.EntityInsentient
import org.bukkit.entity.LivingEntity

class PathfinderGoalMeleeAttackPitchLock(private var mob: CustomMob, private val speed: Double = 1.0) : PathfinderGoal() {
    private var entity: LivingEntity = mob.entity.bukkitEntity as LivingEntity
    private var nmsEntity: EntityInsentient = mob.entity as EntityInsentient
    private val navigation = mob.navigation
    private var target: LivingEntity? = null

    override fun shouldExecute(): Boolean {
        target = nmsEntity.goalTarget?.living ?: return false
        if (target!!.isDead) return false
        return true
    }

    override fun shouldTerminate(): Boolean {
        if (target == null || target!!.isDead) return true
        return false
    }

    override fun init() {
    }

    override fun reset() {
        target = null
    }

    private var cooldown: Int = 0
    override fun execute() {
        mob.lookAtPitchLock(target!!)
        cooldown--
        if (cooldown < 0)
            cooldown = 10
        else return
        if (mob.distanceToEntity(target!!) < entity.width / 2 + 1) {
            target!!.damage(mob.staticTemplate.attackDamage ?: 0.0, entity)
            //TODO knockback
        }
        navigation.moveToEntity(target!!, speed)
    }
}