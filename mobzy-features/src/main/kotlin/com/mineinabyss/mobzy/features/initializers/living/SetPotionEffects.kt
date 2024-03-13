package com.mineinabyss.mobzy.features.initializers.living

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.datatypes.ComponentDefinition
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.papermc.bridge.events.EventHelpers
import com.mineinabyss.geary.papermc.bridge.events.entities.OnSpawn
import com.mineinabyss.geary.serialization.serializers.InnerSerializer
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.serialization.PotionEffectSerializer
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect

@Serializable(with = SetPotionEffects.Serializer::class)
class SetPotionEffects(
    val effects: List<PotionEffect>
) {
    class Serializer : InnerSerializer<List<PotionEffect>, SetPotionEffects>(
        "geary:set.potion_effects",
        ListSerializer(PotionEffectSerializer),
        { SetPotionEffects(it) },
        { it.effects },
    )

    companion object : ComponentDefinition by EventHelpers.defaultTo<OnSpawn>()
}

@AutoScan
fun GearyModule.potionEffectsSetter() = listener(object : ListenerQuery() {
    val bukkit by get<BukkitEntity>()
    val potions by source.get<SetPotionEffects>()
}).exec {
    (bukkit as? LivingEntity)?.addPotionEffects(potions.effects)
}
