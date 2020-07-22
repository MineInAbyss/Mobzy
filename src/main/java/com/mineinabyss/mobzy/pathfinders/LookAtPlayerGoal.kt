package com.mineinabyss.mobzy.pathfinders

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityLiving
import net.minecraft.server.v1_16_R1.EntityInsentient
import net.minecraft.server.v1_16_R1.PathfinderGoalLookAtPlayer
import kotlin.reflect.KClass

class LookAtPlayerGoal(
        mob: EntityInsentient,
        targetType: KClass<out NMSEntityLiving>,
        radius: Float,
        startChance: Float = 0.02f
) : PathfinderGoalLookAtPlayer(mob, targetType.java, radius, startChance)