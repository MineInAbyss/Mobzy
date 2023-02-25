@file:Suppress("NOTHING_TO_INLINE")

package com.mineinabyss.mobzy.pathfinding

import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import net.minecraft.world.entity.Mob

fun Mob.addPathfinderGoal(priority: Int, goal: PathfinderComponent) =
    goalSelector.addGoal(priority, goal.build(toBukkit()))

fun Mob.addTargetSelector(priority: Int, goal: PathfinderComponent) =
    targetSelector.addGoal(priority, goal.build(toBukkit()))
