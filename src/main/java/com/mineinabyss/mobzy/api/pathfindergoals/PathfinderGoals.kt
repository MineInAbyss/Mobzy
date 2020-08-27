package com.mineinabyss.mobzy.api.pathfindergoals

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityInsentient
import com.mineinabyss.mobzy.api.nms.aliases.NMSPathfinderGoal


fun NMSEntityInsentient.addPathfinderGoal(priority: Int, goal: NMSPathfinderGoal) = goalSelector.a(priority, goal)

fun NMSEntityInsentient.removePathfinderGoal(goal: NMSPathfinderGoal) = goalSelector.a(goal)

fun NMSEntityInsentient.addTargetSelector(priority: Int, goal: NMSPathfinderGoal) = targetSelector.a(priority, goal)

fun NMSEntityInsentient.removeTargetSelector(goal: NMSPathfinderGoal) = targetSelector.a(goal)