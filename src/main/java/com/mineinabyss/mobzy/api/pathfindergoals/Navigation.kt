/**
 * Original methods by Yannick Lamprecht under the MIT license from [PathfindergoalAPI](https://github.com/yannicklamprecht/PathfindergoalAPI)
 */
package com.mineinabyss.mobzy.api.pathfindergoals

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityInsentient
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import net.minecraft.server.v1_16_R1.NavigationAbstract
import org.bukkit.entity.Entity
import org.bukkit.entity.Mob

//TODO document

val NavigationAbstract.doneNavigating get() = m()
val NavigationAbstract.inProgress get() = n()
fun NavigationAbstract.moveToPosition(x: Double, y: Double, z: Double, speed: Double) = a(x, y, z, speed)
fun NavigationAbstract.moveToEntity(entity: Entity, speed: Double) = a(entity.toNMS(), speed)
fun NavigationAbstract.setSpeed(speed: Double) = a(speed)
fun NavigationAbstract.stopNavigation() = o()

val Mob.navigation get() = toNMS<NMSEntityInsentient>().navigation