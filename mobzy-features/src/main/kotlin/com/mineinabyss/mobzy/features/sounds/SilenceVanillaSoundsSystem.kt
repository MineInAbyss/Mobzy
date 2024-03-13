package com.mineinabyss.mobzy.features.sounds

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.typealiases.BukkitEntity

@AutoScan
fun GearyModule.createSilenceVanillaSoundsSystem() = listener(object : ListenerQuery() {
    val bukkit by get<BukkitEntity>()
    val sounds by get<Sounds>()
    override fun ensure() = event.anySet(::sounds, ::bukkit)
}).exec {
    bukkit.isSilent = true
}
