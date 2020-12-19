package com.mineinabyss.mobzy.ecs.components.death

import com.mineinabyss.geary.ecs.GearyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
@SerialName("mobzy:death_loot")
class DeathLoot(
        val minExp: Int? = null,
        val maxExp: Int? = null,
        val deathCommands: List<String> = listOf(),
        val drops: List<MobDrop> = listOf()
) : GearyComponent

fun DeathLoot.expToDrop(): Int? {
    val minExp = minExp
    val maxExp = maxExp
    return when {
        minExp == null || maxExp == null -> null
        maxExp <= minExp -> minExp
        else -> Random.nextInt(minExp, maxExp)
    }
}
