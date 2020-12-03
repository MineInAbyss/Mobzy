@file:Suppress("NOTHING_TO_INLINE")

package com.mineinabyss.mobzy.api.pathfindergoals

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityInsentient
import com.mineinabyss.mobzy.api.nms.aliases.NMSPathfinderGoal
import com.mineinabyss.mobzy.api.nms.aliases.NMSPathfinderGoalSelector
import com.mineinabyss.mobzy.api.nms.aliases.toBukkit
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent

inline fun NMSPathfinderGoalSelector.add(goal: NMSPathfinderGoal) = a(goal)
inline fun NMSPathfinderGoalSelector.add(priority: Int, goal: NMSPathfinderGoal) = a(priority, goal)

fun NMSEntityInsentient.addPathfinderGoal(priority: Int, goal: NMSPathfinderGoal) = goalSelector.add(priority, goal)
fun NMSEntityInsentient.addPathfinderGoal(priority: Int, goal: PathfinderComponent) = goalSelector.add(priority, goal.build(toBukkit()))

fun NMSEntityInsentient.removePathfinderGoal(goal: NMSPathfinderGoal) = goalSelector.a(goal)

fun NMSEntityInsentient.addTargetSelector(priority: Int, goal: NMSPathfinderGoal) = targetSelector.add(priority, goal)
fun NMSEntityInsentient.addTargetSelector(priority: Int, goal: PathfinderComponent) = targetSelector.add(priority, goal.build(toBukkit()))

fun NMSEntityInsentient.removeTargetSelector(goal: NMSPathfinderGoal) = targetSelector.add(goal)
