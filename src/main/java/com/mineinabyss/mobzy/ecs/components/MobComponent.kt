package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.MobzyComponent
import org.bukkit.entity.Mob

data class MobComponent(
        val mob: Mob
) : MobzyComponent()