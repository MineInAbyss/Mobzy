package com.mineinabyss.mobzy.features.sounds

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.systems.builders.system
import com.mineinabyss.geary.systems.query.Query
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.features.sounds.OverrideMobSoundsBukkitListener.Companion.makeSound
import kotlin.random.Random

@AutoScan
fun GearyModule.createAmbientSoundsSystem() = system(object : Query() {
    val bukkit by get<BukkitEntity>()
    val sounds by get<Sounds>()
}).every(1.ticks).exec {
    if (Random.nextDouble() < sounds.ambientChance)
        makeSound(bukkit, sounds.ambient)
}
