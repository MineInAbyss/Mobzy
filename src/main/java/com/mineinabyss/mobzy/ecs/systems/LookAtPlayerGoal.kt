package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.mobzy.api.nms.aliases.NMSPlayer
import com.mineinabyss.mobzy.mobs.AnyCustomMob
import net.minecraft.server.v1_16_R1.PathfinderGoalLookAtPlayer

class LookAtPlayerGoal(
        mob: AnyCustomMob,
        radius: Float,
        startChance: Float = 0.02f
) : PathfinderGoalLookAtPlayer(mob.nmsEntity, NMSPlayer::class.java, radius, startChance)
