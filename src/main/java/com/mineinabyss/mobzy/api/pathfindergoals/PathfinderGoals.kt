package com.mineinabyss.mobzy.api.pathfindergoals

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityInsentient
import net.minecraft.server.v1_16_R1.PathfinderGoal
import net.minecraft.server.v1_16_R1.PathfinderGoalTarget


fun NMSEntityInsentient.addPathfinderGoal(priority: Int, goal: PathfinderGoal) = goalSelector.a(priority, goal)

fun NMSEntityInsentient.removePathfinderGoal(goal: PathfinderGoal) = goalSelector.a(goal)

fun NMSEntityInsentient.addTargetSelector(priority: Int, goal: PathfinderGoalTarget) = targetSelector.a(priority, goal)

fun NMSEntityInsentient.removeTargetSelector(goal: PathfinderGoalTarget) = targetSelector.a(goal)