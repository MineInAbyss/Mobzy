package com.mineinabyss.mobzy.injection.types

import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.NMSWorld
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.idofront.nms.entity.typeName
import com.mineinabyss.mobzy.injection.CustomEntity
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EnumMoveType
import net.minecraft.world.phys.Vec3D

class InteractableEntity(type: NMSEntityType<*>, world: NMSWorld) : PassiveMob(type, world), CustomEntity {
    //Stop from being pushed around
    override fun move(movementType: EnumMoveType, movement: Vec3D) = Unit
    override fun collide(entity: NMSEntity) = Unit

    //Prevent NPCs from getting damaged by anything
    override fun damageEntity(damagesource: DamageSource, f: Float) = false

    init {
        //TODO CustomName component perhaps?
        toBukkit().customName = entityType.typeName
        customNameVisible = false
        isInvulnerable = true
        addScoreboardTag("interactable")
    }
}