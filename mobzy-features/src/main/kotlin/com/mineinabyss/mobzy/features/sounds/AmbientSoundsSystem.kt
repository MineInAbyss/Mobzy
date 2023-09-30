package com.mineinabyss.mobzy.features.sounds

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.RepeatingSystem
import com.mineinabyss.geary.systems.accessors.Pointer
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlin.random.Random

@AutoScan
class AmbientSoundsSystem : RepeatingSystem(interval = 1.ticks) {
    private val Pointer.sounds by get<Sounds>()
    private val Pointer.bukkit by get<BukkitEntity>()

    override fun Pointer.tick() {
        if (Random.nextDouble() < sounds.ambientChance)
            OverrideMobSoundsSystem.makeSound(bukkit, sounds.ambient)
    }
}
