package com.offz.spigot.abyssalcreatures.mobs

import net.minecraft.server.v1_15_R1.*
import net.minecraft.server.v1_15_R1.MovingObjectPosition.EnumMovingObjectType

class ThrownRock(world: World?, thrower: EntityLiving): EntitySnowball(world, thrower) {
    override fun a(mop: MovingObjectPosition) {
        super.a(mop)

        if (mop.type == EnumMovingObjectType.ENTITY) {
            val hit = (mop as MovingObjectPositionEntity).entity
            if (hit is EntityPlayer)
                hit.damageEntity(DamageSource.projectile(this, getShooter()), 0.5f)
        }
    }
}