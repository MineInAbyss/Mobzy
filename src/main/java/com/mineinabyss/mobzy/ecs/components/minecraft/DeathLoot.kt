package com.mineinabyss.mobzy.ecs.components.minecraft

import com.mineinabyss.mobzy.ecs.components.MobDrop
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.ecs.components.MobzyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:death_loot")
class DeathLoot(
        val minExp: Int? = null,
        val maxExp: Int? = null,
        val deathCommands: List<String> = listOf(),
        val drops: List<MobDrop> = listOf()
): MobzyComponent

val MobType.deathLoot get() = get<DeathLoot>()