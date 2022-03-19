package com.mineinabyss.mobzy.injection.types

import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.NMSWorld
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.mobzy.injection.CustomEntity
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.phys.Vec3

context(GearyMCContext)
class NPC(type: NMSEntityType<out Animal>, world: NMSWorld) : PassiveMob(type, world), CustomEntity {
    //Stop from being pushed around
    override fun setDeltaMovement(velocity: Vec3) = Unit
//    override fun move(movementType: EnumMoveType, movement: Vec3D) = Unit
//    override fun collide(entity: NMSEntity) = Unit

    //Prevent NPCs from getting damaged by anything
    override fun damageEntity0(damagesource: DamageSource, f: Float): Boolean = false

    init {
        isInvulnerable = true
        toBukkit().addScoreboardTag("npc")
    }
}
