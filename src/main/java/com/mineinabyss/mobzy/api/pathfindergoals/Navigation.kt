/**
 * Original methods by Yannick Lamprecht under the MIT license from [PathfindergoalAPI](https://github.com/yannicklamprecht/PathfindergoalAPI)
 */
package com.mineinabyss.mobzy.api.pathfindergoals

import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import net.minecraft.server.v1_16_R2.NavigationAbstract
import org.bukkit.entity.Entity

/** Whether the entity has finished navigating to its destination */
val NavigationAbstract.doneNavigating get() = m()

/** Whether navigation is currently in progress */
val NavigationAbstract.inProgress get() = n()

/** Moves an entity to the position defined at [x], [y], [z], with a specified [speed] */
fun NavigationAbstract.moveToPosition(x: Double, y: Double, z: Double, speed: Double) = a(x, y, z, speed)

/** Moves to [entity], with a specified [speed]*/
fun NavigationAbstract.moveToEntity(entity: Entity, speed: Double) = a(entity.toNMS(), speed)

/** Sets a speed multiplier with which to navigate */
fun NavigationAbstract.setSpeed(speed: Double) = a(speed)

/** Stops the current navigation */
fun NavigationAbstract.stopNavigation() = o()