package com.mineinabyss.mobzy.features.sounds

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.typealiases.BukkitEntity

@AutoScan
class SilenceVanillaSoundsSystem : GearyListener() {
    private val TargetScope.bukkit by onSet<BukkitEntity>()
    private val TargetScope.sounds by onSet<Sounds>()

    @Handler
    fun TargetScope.removeSounds() {
        bukkit.isSilent = true
    }
}
