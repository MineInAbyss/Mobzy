package com.mineinabyss.mobzy.ecs.components.minecraft

import com.mineinabyss.mobzy.ecs.components.MobzyComponent
import com.mineinabyss.mobzy.mobs.AnyCustomMob
import org.bukkit.entity.Entity
import org.bukkit.entity.Mob

data class EntityComponent(
        val entity: Mob
) : MobzyComponent