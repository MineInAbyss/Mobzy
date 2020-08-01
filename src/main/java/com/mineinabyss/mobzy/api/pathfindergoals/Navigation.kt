package com.mineinabyss.mobzy.api.pathfindergoals

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityInsentient
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityLiving
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import net.minecraft.server.v1_16_R1.GenericAttributes
import net.minecraft.server.v1_16_R1.NavigationAbstract
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob

/**
 * Original methods by Yannick Lamprecht under the MIT license from [PathfindergoalAPI](https://github.com/yannicklamprecht/PathfindergoalAPI)
 */
//TODO document
class Navigation(private val navigationAbstract: NavigationAbstract, private val handle: NMSEntityInsentient) {
    val doneNavigating get() = navigationAbstract.m()
    val inProgress get() = navigationAbstract.n()
    val pathSearchRange get() = handle.getAttributeInstance(GenericAttributes.FOLLOW_RANGE)?.value?.toFloat()
    fun moveToPosition(x: Double, y: Double, z: Double, speed: Double) = navigationAbstract.a(x, y, z, speed)
    fun moveToEntity(entity: Entity, speed: Double) = navigationAbstract.a(entity.toNMS(), speed)
    fun setSpeed(speed: Double) = navigationAbstract.a(speed)
    fun stopNavigation() = navigationAbstract.o()
}

val Mob.navigation get() = toNMS<NMSEntityInsentient>().navigation
val NMSEntityInsentient.navigation get() = Navigation(this.navigation, this)