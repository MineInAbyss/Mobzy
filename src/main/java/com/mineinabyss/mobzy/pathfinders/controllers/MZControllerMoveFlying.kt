package com.mineinabyss.mobzy.pathfinders.controllers

import com.mineinabyss.mobzy.mobs.types.FlyingMob
import net.minecraft.server.v1_15_R1.AxisAlignedBB
import net.minecraft.server.v1_15_R1.ControllerMove
import net.minecraft.server.v1_15_R1.MathHelper
import net.minecraft.server.v1_15_R1.Vec3D
import kotlin.random.Random

/**
 * From EntityGhast's controller
 * TODO stop relying on NMS code
 */
class MZControllerMoveFlying(private val mob: FlyingMob) : ControllerMove(mob) {
    private var j = 0
    override fun a() {
        if (h == Operation.MOVE_TO && j-- <= 0) {
            j += Random.nextInt(2, 7)
            var vec3d = Vec3D(b - mob.locX, c - mob.locY, d - mob.locZ)
            vec3d = vec3d.d()
            if (this.hasLineOfSight(vec3d, MathHelper.f(vec3d.f()))) {
                val speed: Double = mob.staticTemplate.movementSpeed ?: 0.1
                mob.mot = mob.mot.e(vec3d.a(speed))
            } else h = Operation.WAIT
        }
    }

    fun stopNavigation() {
        h = Operation.WAIT
    }

    private fun hasLineOfSight(vec3d: Vec3D, distance: Int): Boolean {
        var axisalignedbb: AxisAlignedBB = mob.boundingBox
        for (j in 1 until distance) {
            axisalignedbb = axisalignedbb.b(vec3d)
            if (!mob.world.getCubes(mob, axisalignedbb)) {
                return false
            }
        }
        return true
    }


}
