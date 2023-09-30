package com.mineinabyss.mobzy.features.initializers

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.LivingEntity

/**
 * > mobzy:remove_when_far_away
 *
 * Specifies this entity should get removed when it is far away from any player.
 */
@Serializable
@SerialName("mobzy:remove_when_far_away")
class SetRemoveWhenFarAway(val value: Boolean)

@AutoScan
class SetRemoveWhenFarAwaySystem : GearyListener() {
    private val Pointers.removeWhenFarAway by get<SetRemoveWhenFarAway>().whenSetOnTarget()
    private val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()

    override fun Pointers.handle() {
        val living = bukkit as? LivingEntity ?: return
        living.removeWhenFarAway = removeWhenFarAway.value
    }
}
