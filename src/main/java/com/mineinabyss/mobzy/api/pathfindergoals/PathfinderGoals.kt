@file:Suppress("NOTHING_TO_INLINE")

package com.mineinabyss.mobzy.api.pathfindergoals

import com.mineinabyss.idofront.nms.aliases.NMSEntityInsentient
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.idofront.nms.pathfindergoals.add
import com.mineinabyss.idofront.nms.pathfindergoals.goalSelector
import com.mineinabyss.idofront.nms.pathfindergoals.targetSelector
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent

fun NMSEntityInsentient.addPathfinderGoal(priority: Int, goal: PathfinderComponent) =
    goalSelector.add(priority, goal.build(toBukkit()))

fun NMSEntityInsentient.addTargetSelector(priority: Int, goal: PathfinderComponent) =
    targetSelector.add(priority, goal.build(toBukkit()))
