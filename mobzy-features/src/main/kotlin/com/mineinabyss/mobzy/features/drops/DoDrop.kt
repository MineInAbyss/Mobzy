package com.mineinabyss.mobzy.features.drops

import com.mineinabyss.geary.datatypes.ComponentDefinition
import com.mineinabyss.geary.papermc.bridge.events.EventHelpers
import com.mineinabyss.geary.papermc.bridge.events.entities.OnDeath
import com.mineinabyss.geary.serialization.serializers.InnerSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer


/**
 * A component for loot that should drop on entity death.
 *
 * @param minExp The minimum amount of exp to drop.
 * @param maxExp The maximum amount of exp to drop.
 * @param deathCommands A list of commands to run.
 * @param drops A list of [Drop]s to spawn.
 */
@Serializable(with = DoDrop.Serializer::class)
class DoDrop(
    val drops: List<Drop> = listOf(),
) {
    class Serializer : InnerSerializer<List<Drop>, DoDrop>(
        "mobzy:drop",
        ListSerializer(Drop.serializer()),
        { DoDrop(it) },
        { it.drops },
    )

    companion object : ComponentDefinition by EventHelpers.defaultTo<OnDeath>()
}
