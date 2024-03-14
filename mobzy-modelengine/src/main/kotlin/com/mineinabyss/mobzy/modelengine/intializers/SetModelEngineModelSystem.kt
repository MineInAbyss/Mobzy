package com.mineinabyss.mobzy.modelengine.intializers

import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.mobzy.modelengine.intializers.ModelEngineWorldListener.Companion.ensureModelLoaded
import org.bukkit.Bukkit

fun GearyModule.createModelEngineListener() = listener(
    object : ListenerQuery() {
        val bukkit by get<BukkitEntity>()
        val model by get<SetModelEngineModel>()
        override fun ensure() = event.anySet(::bukkit, ::model)
    }
).exec {
    val bukkit = bukkit
    val model = model
    Bukkit.getScheduler().scheduleSyncDelayedTask(mobzy.plugin, {
        ensureModelLoaded(bukkit, model)
    }, 1L)
}
