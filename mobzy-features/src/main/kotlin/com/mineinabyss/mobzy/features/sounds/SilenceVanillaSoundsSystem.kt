package com.mineinabyss.mobzy.features.sounds

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.typealiases.BukkitEntity

@AutoScan
class SilenceVanillaSoundsSystem : GearyListener() {
    private val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()
    private val Pointers.sounds by get<Sounds>().whenSetOnTarget()

    override fun Pointers.handle() {
        bukkit.isSilent = true
    }
}
