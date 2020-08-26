package com.mineinabyss.mobzy.api.nms.goalwrappers

import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import net.minecraft.server.v1_16_R2.PathfinderGoalHurtByTarget
import org.bukkit.entity.Creature

class HurtByTargetGoal(creature: Creature, vararg ignore: Class<*>): PathfinderGoalHurtByTarget(creature.toNMS(), *ignore)