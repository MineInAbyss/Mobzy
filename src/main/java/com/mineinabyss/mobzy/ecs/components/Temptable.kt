package com.mineinabyss.mobzy.ecs.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
@SerialName("mobzy:temptable")
class Temptable(
        val items: List<Material>
): MobzyComponent

//TODO better name
val AnyMobType.temptItems get() = get<Temptable>()