package com.mineinabyss.mobzy

import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.mineinabyss.geary.autoscan.autoscan
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.features.Feature
import com.mineinabyss.idofront.features.FeatureDSL
import com.mineinabyss.idofront.features.FeatureManager
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.plugin.actions
import com.mineinabyss.idofront.plugin.listeners
import com.mineinabyss.mobzy.features.despawning.RemoveOnChunkUnloadSystem
import com.mineinabyss.mobzy.features.displayname.ShowDisplayNameOnKillerSystem
import com.mineinabyss.mobzy.features.prevent.breeding.PreventBreedingSystem
import com.mineinabyss.mobzy.features.prevent.interaction.PreventInteractionSystem
import com.mineinabyss.mobzy.features.prevent.regen.PreventRegenerationSystem
import com.mineinabyss.mobzy.features.prevent.riding.PreventRidingSystem
import com.mineinabyss.mobzy.features.sounds.OverrideMobSoundsBukkitListener
import com.mineinabyss.mobzy.features.taming.TamingBukkitListener
import com.mineinabyss.mobzy.modelengine.ModelEngineSupport
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import com.mineinabyss.mobzy.spawning.MobzySpawnFeature
import com.mineinabyss.mobzy.spawning.WorldGuardSpawnFlags
import kotlinx.coroutines.cancel
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class MobzyPlugin : JavaPlugin() {
    override fun onLoad() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            val flags = WorldGuardSpawnFlags()
            DI.add<WorldGuardSpawnFlags>(flags)
            flags.registerFlags()
        }

        createMobzyContext()

        geary {
            autoscan(classLoader, "com.mineinabyss.mobzy") {
                all()
                subClassesOf<PathfinderComponent>()
            }

            if (Plugins.isEnabled("ModelEngine")) {
                install(ModelEngineSupport)
            }
        }
    }

    override fun onEnable() = actions {
        MobzyFeatureManager(this@MobzyPlugin).enable()
        MobzyCommands()

        listeners(
            PreventBreedingSystem(),
            RemoveOnChunkUnloadSystem(),
            ShowDisplayNameOnKillerSystem(),
            TamingBukkitListener(),
            PreventRidingSystem(),
            PreventRegenerationSystem(),
            PreventInteractionSystem(),
            OverrideMobSoundsBukkitListener(),
        )

        geary {
            if (Plugins.isEnabled("ModelEngine")) {
                install(ModelEngineSupport)
            }
        }
    }

    override fun onDisable() {
        super.onDisable()
        minecraftDispatcher.cancel()
    }

    fun createMobzyContext() {
        DI.remove<MobzyModule>()
        DI.add<MobzyModule>(object : MobzyModule {
            override val plugin: MobzyPlugin = this@MobzyPlugin
            override val config: MobzyConfig by config("config", dataFolder.toPath(), MobzyConfig())
        })
    }

    class MobzyContext(override val plugin: JavaPlugin) : FeatureDSL() {
        override val features: List<Feature> = buildList {
            if (mobzy.config.doMobSpawns) add(MobzySpawnFeature())
        }
    }

    class MobzyFeatureManager(plugin: JavaPlugin) : FeatureManager<MobzyContext>(plugin, { MobzyContext(plugin) })
}
