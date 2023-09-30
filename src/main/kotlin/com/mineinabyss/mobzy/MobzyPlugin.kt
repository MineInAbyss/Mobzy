package com.mineinabyss.mobzy

import com.mineinabyss.geary.autoscan.autoscan
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.platforms.Platforms
import com.mineinabyss.idofront.plugin.ActionScope
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.plugin.listeners
import com.mineinabyss.mobzy.features.breeding.PreventBreedingSystem
import com.mineinabyss.mobzy.features.copynbt.CopyNBTSystem
import com.mineinabyss.mobzy.features.deathloot.DeathLootSystem
import com.mineinabyss.mobzy.features.despawning.RemoveOnChunkUnloadSystem
import com.mineinabyss.mobzy.features.displayname.ShowDisplayNameOnKillerSystem
import com.mineinabyss.mobzy.features.nointeractions.DisableMobInteractionsSystem
import com.mineinabyss.mobzy.features.riding.PreventRidingSystem
import com.mineinabyss.mobzy.features.sounds.OverrideMobSoundsSystem
import com.mineinabyss.mobzy.features.taming.TamableListener
import com.mineinabyss.mobzy.modelengine.ModelEngineSupport
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import com.mineinabyss.mobzy.spawning.MobzySpawning
import com.mineinabyss.mobzy.spawning.WorldGuardSpawnFlags
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class MobzyPlugin : JavaPlugin() {
    override fun onLoad() {
        Platforms.load(this, "mineinabyss")
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            val flags = WorldGuardSpawnFlags()
            DI.add<WorldGuardSpawnFlags>(flags)
            flags.registerFlags()
        }
    }

    inline fun actions(run: ActionScope.() -> Unit) {
        ActionScope().apply(run)
    }

    override fun onEnable() = actions {
        DI.add<MobzyModule>(object : MobzyModule {
            override val plugin: MobzyPlugin = this@MobzyPlugin
            override val config: MobzyConfig by config("config") {
                fromPluginPath(loadDefault = true)
            }
        })

        MobzyCommands()

        geary {
            if (mobzy.config.doMobSpawns) {
                if (!Plugins.isEnabled("WorldGuard"))
                    logger.warning("Could not load spawning module, WorldGuard is not installed.")
                else install(MobzySpawning)
            }

            autoscan(classLoader, "com.mineinabyss.mobzy") {
                all()
                subClassesOf<PathfinderComponent>()
            }

            if (Plugins.isEnabled("ModelEngine")) {
                install(ModelEngineSupport)
            }
        }

        listeners(
            PreventBreedingSystem(),
            DeathLootSystem(),
            RemoveOnChunkUnloadSystem(),
            ShowDisplayNameOnKillerSystem(),
            TamableListener(),
            PreventRidingSystem(),
            DisableMobInteractionsSystem(),
            OverrideMobSoundsSystem(),
        )

        geary.pipeline.addSystems(
            CopyNBTSystem(),
        )
    }
}
