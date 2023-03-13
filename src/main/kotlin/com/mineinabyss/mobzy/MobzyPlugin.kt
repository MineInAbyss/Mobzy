package com.mineinabyss.mobzy

import com.mineinabyss.geary.addons.GearyPhase
import com.mineinabyss.geary.autoscan.autoscan
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.geary.papermc.tracking.entities.entityTracking
import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.platforms.Platforms
import com.mineinabyss.idofront.plugin.*
import com.mineinabyss.mobzy.features.breeding.PreventBreedingSystem
import com.mineinabyss.mobzy.features.copynbt.CopyNBTSystem
import com.mineinabyss.mobzy.features.deathloot.DeathLootSystem
import com.mineinabyss.mobzy.features.despawning.RemoveOnChunkUnloadSystem
import com.mineinabyss.mobzy.features.displayname.ShowDisplayNameOnKillerSystem
import com.mineinabyss.mobzy.features.nointeractions.DisableMobInteractionsSystem
import com.mineinabyss.mobzy.features.riding.PreventRidingSystem
import com.mineinabyss.mobzy.features.taming.TamableListener
import com.mineinabyss.mobzy.modelengine.ModelEngineSupport
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import com.mineinabyss.mobzy.spawning.MobzySpawning
import com.mineinabyss.mobzy.spawning.WorldGuardSpawnFlags
import com.mineinabyss.mobzy.spawning.mobzySpawning
import com.ticxo.modelengine.api.ModelEngineAPI
import org.bukkit.plugin.java.JavaPlugin

class MobzyPlugin : JavaPlugin() {
    override fun onLoad() {
        Platforms.load(this, "mineinabyss")
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
            if (mobzy.config.doMobSpawns) install(MobzySpawning)

            autoscan(classLoader, "com.mineinabyss") {
                all()
                subClassesOf<PathfinderComponent>()
            }
            on(GearyPhase.ENABLE) {
                logSuccess("Loaded types: ${entityTracking.mobPrefabs.getKeys()}")
            }

            if (Plugins.isEnabled<ModelEngineAPI>()) {
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
            DisableMobInteractionsSystem()
        )

        geary.pipeline.addSystems(
            CopyNBTSystem(),
        )
    }
}
