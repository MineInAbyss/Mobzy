package com.mineinabyss.mobzy.features.initializers.living

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.datatypes.ComponentDefinition
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.papermc.bridge.events.EventHelpers
import com.mineinabyss.geary.papermc.bridge.events.entities.OnSpawn
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.LivingEntity

/**
 * Specifies this entity should get removed when it is far away from any player.
 */
@Serializable
@SerialName("mobzy:set.remove_when_far_away")
class SetRemoveWhenFarAway(val value: Boolean = true) {
    companion object : ComponentDefinition by EventHelpers.defaultTo<OnSpawn>()
}

@AutoScan
fun GearyModule.removeWhenFarAwaySetter() = listener(object : ListenerQuery() {
    val bukkit by get<BukkitEntity>()
    val removeWhenFarAway by source.get<SetRemoveWhenFarAway>()
}).exec {
    val living = bukkit as? LivingEntity ?: return@exec
    living.removeWhenFarAway = removeWhenFarAway.value
}
