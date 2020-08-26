package com.mineinabyss.mobzy.api.nms.goalwrappers

import com.mineinabyss.mobzy.api.nms.aliases.NMSPlayer
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import net.minecraft.server.v1_16_R2.PathfinderGoalLookAtPlayer
import org.bukkit.entity.Mob


class LookAtPlayerGoal(
        entity: Mob,
        radius: Float,
        startChance: Float = 0.02f
) : PathfinderGoalLookAtPlayer(entity.toNMS(), NMSPlayer::class.java, radius, startChance)
