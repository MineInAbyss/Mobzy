package com.mineinabyss.mobzy.features.deathloot

import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.idofront.util.randomOrMin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.event.entity.EntityDamageEvent.DamageCause

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
class DeathLoot(
    val exp: @Serializable(with = IntRangeSerializer::class) IntRange? = null,
    val deathCommands: List<String> = listOf(),
    val drops: List<MobDrop> = listOf(),
    val ignoredCauses: List<DamageCause> = listOf(
        DamageCause.SUFFOCATION,
        DamageCause.DROWNING,
        DamageCause.DRYOUT,
        DamageCause.CRAMMING,
        DamageCause.FALL
    ),
) {
    /** Helper function for randomly picking some amount of exp to drop. */
    fun expToDrop(): Int? = exp?.randomOrMin()
}
