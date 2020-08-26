package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.mobs.CustomMob
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
) : MobzyComponent

val CustomMob.deathLoot get() = get<DeathLoot>()

fun DeathLoot.expToDrop(): Int? {
    val minExp = minExp
    val maxExp = maxExp
    return when {
        minExp == null || maxExp == null -> null
        maxExp <= minExp -> minExp
        else -> Random.nextInt(minExp, maxExp)
    }
}