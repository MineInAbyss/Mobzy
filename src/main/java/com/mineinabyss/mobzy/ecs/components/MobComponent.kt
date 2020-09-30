package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.GearyComponent
import org.bukkit.entity.Mob

data class MobComponent(
        val mob: Mob
) : GearyComponent()