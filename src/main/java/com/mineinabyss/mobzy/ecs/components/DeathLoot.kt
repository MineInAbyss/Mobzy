package com.mineinabyss.mobzy.ecs.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:deathLoot")
class DeathLoot(
        val minExp: Int? = null,
        val maxExp: Int? = null,
        val deathCommands: List<String> = listOf(),
        val drops: List<MobDrop> = listOf()
): MobzyComponent

val AnyMobType.deathLoot get() = get<DeathLoot>()