package com.mineinabyss.mobzy.features.sounds

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.RepeatingSystem
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlin.random.Random

@AutoScan
class AmbientSoundsSystem : RepeatingSystem(interval = 1.ticks) {
    private val TargetScope.sounds by get<Sounds>()
    private val TargetScope.bukkit by get<BukkitEntity>()

    override fun TargetScope.tick() {
        if (Random.nextDouble() < sounds.ambientChance)
            OverrideMobSoundsSystem.makeSound(bukkit, sounds.ambient)
    }
}
