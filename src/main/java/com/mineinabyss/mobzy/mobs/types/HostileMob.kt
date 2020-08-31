package com.mineinabyss.mobzy.mobs.types

import com.mieninabyss.mobzy.processor.GenerateFromBase
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.aliases.NMSWorld
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.api.pathfindergoals.addTargetSelector
import com.mineinabyss.mobzy.ecs.goals.minecraft.MeleeAttackBehavior
import net.minecraft.server.v1_16_R2.*


/**
 * Lots of code taken from EntityZombie
 */
@GenerateFromBase(base = MobBase::class, createFor = [EntityMonster::class])
open class HostileMob(type: NMSEntityType<*>, world: NMSWorld) : MobzyEntityMonster(world, type) {
    override fun createPathfinders() {
        addPathfinderGoal(2, MeleeAttackBehavior(attackSpeed = 1.0, seeThroughWalls = false).build(entity))
        addPathfinderGoal(3, PathfinderGoalFloat(this))
        addPathfinderGoal(7, PathfinderGoalRandomStrollLand(this, 1.0))
        addPathfinderGoal(7, PathfinderGoalLookAtPlayer(this, EntityPlayer::class.java, 8.0f))
        addPathfinderGoal(8, PathfinderGoalRandomLookaround(this))

        addTargetSelector(2, PathfinderGoalNearestAttackableTarget(this, EntityHuman::class.java, true))
    }

    /** Removes entity if not in peaceful mode */
    override fun tick() = super.tick().also { if (!world.isClientSide && world.difficulty == EnumDifficulty.PEACEFUL) die() }

    init {
        initMob()
        addScoreboardTag("hostileMob")
        entity.removeWhenFarAway = true
        attributeMap
    }
}