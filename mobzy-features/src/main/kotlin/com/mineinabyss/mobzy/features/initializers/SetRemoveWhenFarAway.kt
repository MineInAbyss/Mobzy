package com.mineinabyss.mobzy.features.initializers

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
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
    private val TargetScope.removeWhenFarAway by onSet<SetRemoveWhenFarAway>()
    private val TargetScope.bukkit by onSet<BukkitEntity>()

    @Handler
    fun TargetScope.setRemoveWhenFarAway() {
        val living = bukkit as? LivingEntity
            ?: error("Cannot set remove when far away on non-living entity: $bukkit")
        living.removeWhenFarAway = removeWhenFarAway.value
    }
}
