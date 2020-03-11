package com.mineinabyss.mobzy.pathfinders

import net.minecraft.server.v1_15_R1.*
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

/**
 * Original methods by Yannick Lamprecht under the MIT license from https://github.com/yannicklamprecht/PathfindergoalAPI
 */

class Navigation(private val navigationAbstract: NavigationAbstract, private val handle: EntityInsentient) {

    val doneNavigating
        get() = navigationAbstract.n()

    val pathSearchRange
        get() = handle.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).value.toFloat()

    fun moveToPosition(x: Double, y: Double, z: Double, speed: Double) = navigationAbstract.a(x, y, z, speed)
    fun moveToEntity(entity: Entity, speed: Double) = navigationAbstract.a((entity as CraftEntity).handle, speed)
    fun speed(speed: Double) = navigationAbstract.a(speed)
    fun clearPathEntity(): PathfinderAbstract = navigationAbstract.q()
    fun isComplete(): Boolean = navigationAbstract.m()
}

val EntityInsentient.navigationMZ
    get() = Navigation(this.navigation, this)

val EntityLiving.living
    get() = this.bukkitEntity as LivingEntity