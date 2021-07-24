package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.NMSWorld
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.idofront.nms.entity.typeName
import com.mineinabyss.idofront.nms.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.ecs.goals.minecraft.LookAtPlayerBehavior
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EnumMoveType
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround
import net.minecraft.world.phys.Vec3D

class NPC(type: NMSEntityType<*>, world: NMSWorld) : PassiveMob(type, world) {
    //Stop from being pushed around
    override fun move(enummovetype: EnumMoveType?, vec3d: Vec3D?) = Unit

    override fun collide(entity: NMSEntity) = Unit

    //Prevent NPCs from getting damaged by anything
    override fun damageEntity(damagesource: DamageSource, f: Float) = false

    override fun createPathfinders() {
        addPathfinderGoal(2, PathfinderGoalRandomLookaround(this))
        addPathfinderGoal(7, LookAtPlayerBehavior(radius = 8f).build(this.toBukkit()))
    }

    init {
        //TODO CustomName component perhaps?
        entity.customName = nmsEntity.entityType.typeName
        customNameVisible = true
        isInvulnerable = true
        entity.removeWhenFarAway = false
        addScoreboardTag("npc")
    }
}
