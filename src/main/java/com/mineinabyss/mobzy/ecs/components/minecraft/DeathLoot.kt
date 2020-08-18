package com.mineinabyss.mobzy.ecs.components.minecraft

import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.ecs.components.MobDrop
import com.mineinabyss.mobzy.ecs.components.get
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

fun CustomMob.expToDrop(): Int {
    val minExp = deathLoot?.minExp
    val maxExp = deathLoot?.maxExp
    return when { //TODO move into system
        minExp == null || maxExp == null -> nmsEntity.expToDrop
        maxExp <= minExp -> minExp
        else -> Random.nextInt(minExp, maxExp)
    }
}

val CustomMob.deathLoot get() = get<DeathLoot>()