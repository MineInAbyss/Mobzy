@file:Suppress("NOTHING_TO_INLINE")

package com.mineinabyss.mobzy.pathfinding

import com.mineinabyss.idofront.nms.aliases.NMSPathfinderMob
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent

fun NMSPathfinderMob.addPathfinderGoal(priority: Int, goal: PathfinderComponent) =
    goalSelector.addGoal(priority, goal.build(toBukkit()))

fun NMSPathfinderMob.addTargetSelector(priority: Int, goal: PathfinderComponent) =
    targetSelector.addGoal(priority, goal.build(toBukkit()))
