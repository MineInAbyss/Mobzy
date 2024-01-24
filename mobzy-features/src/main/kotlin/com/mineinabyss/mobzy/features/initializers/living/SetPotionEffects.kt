package com.mineinabyss.mobzy.features.initializers.living

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.serialization.serializers.InnerSerializer
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
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
    class Serializer: InnerSerializer<List<PotionEffect>, SetPotionEffects>(
        "geary:set.potion_effects",
        ListSerializer(PotionEffectSerializer),
        { SetPotionEffects(it) },
        { it.effects },
    )
}

@AutoScan
class SetPotionEffectsSystem : GearyListener() {
    val Pointers.bukkit by get<BukkitEntity>().on(target)
    val Pointers.potions by get<SetPotionEffects>().on(source)

    override fun Pointers.handle() {
        (bukkit as? LivingEntity)?.addPotionEffects(potions.effects)
    }
}
