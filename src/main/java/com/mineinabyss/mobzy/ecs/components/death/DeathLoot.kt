package com.mineinabyss.mobzy.ecs.components.death

import com.mineinabyss.geary.ecs.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.random.Random

/**
 * A component for loot that should drop on entity death.
 *
 * @param minExp The minimum amount of exp to drop.
 * @param maxExp The maximum amount of exp to drop.
 * @param deathCommands A list of commands to run.
 * @param drops A list of [MobDrop]s to spawn.
 */
@Serializable
@SerialName("mobzy:death_loot")
@AutoscanComponent
class DeathLoot(
    val minExp: Int? = null,
    val maxExp: Int? = null,
    val deathCommands: List<String> = listOf(),
    val drops: List<MobDrop> = listOf()
) {
    /** Helper function for randomly picking some amount of exp to drop. */
    fun expToDrop(): Int? {
        return when {
            minExp == null || maxExp == null -> null
            maxExp <= minExp -> minExp
            else -> Random.nextInt(minExp, maxExp)
        }
    }
}
