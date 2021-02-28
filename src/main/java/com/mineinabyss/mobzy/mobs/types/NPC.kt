package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.aliases.NMSWorld
import com.mineinabyss.mobzy.api.nms.aliases.toBukkit
import com.mineinabyss.mobzy.api.nms.entity.typeName
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.ecs.goals.minecraft.LookAtPlayerBehavior
import net.minecraft.server.v1_16_R2.DamageSource
import net.minecraft.server.v1_16_R2.EnumMoveType
import net.minecraft.server.v1_16_R2.PathfinderGoalRandomLookaround
import net.minecraft.server.v1_16_R2.Vec3D

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
