package com.mineinabyss.mobzy.ecs.components.minecraft

import com.mineinabyss.mobzy.ecs.components.MobzyComponent
import com.mineinabyss.mobzy.mobs.AnyCustomMob

data class EntityComponent(
        val entity: AnyCustomMob
) : MobzyComponent