package com.mineinabyss.mobzy.ecs.components.interaction

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import org.bukkit.Location

@AutoscanComponent
data class ProjectileShootAt(
    val location: Location
)
