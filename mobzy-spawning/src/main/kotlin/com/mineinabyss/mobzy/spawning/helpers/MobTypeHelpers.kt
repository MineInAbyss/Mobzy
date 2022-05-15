package com.mineinabyss.mobzy.spawning.helpers

import com.mineinabyss.geary.papermc.helpers.customMobType
import org.bukkit.entity.Entity

//TODO perhaps give normal mobs prefab keys too to make this more type safe
fun Collection<Entity>.categorizeMobs(): Map<String, Int> =
    groupingBy { it.customMobType }.eachCount()
