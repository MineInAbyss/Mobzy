package com.mineinabyss.mobzy.api.nms.goalwrappers

import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import net.minecraft.server.v1_16_R1.PathfinderGoalFloat
import org.bukkit.entity.Mob

class FloatGoal(entity: Mob) : PathfinderGoalFloat(entity.toNMS())