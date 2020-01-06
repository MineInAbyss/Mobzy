package com.offz.spigot.mobzy.gui

import com.derongan.minecraft.guiy.gui.Cell
import com.derongan.minecraft.guiy.gui.ClickableElement
import com.derongan.minecraft.guiy.gui.FillableElement
import com.derongan.minecraft.guiy.gui.Layout
import com.derongan.minecraft.guiy.gui.layouts.HistoryGuiHolder
import com.offz.spigot.mobzy.gui.layouts.MobConfigLayout
import com.offz.spigot.mobzy.mobTemplate
import com.offz.spigot.mobzy.mobzy
import com.offz.spigot.mobzy.spawning.SpawnRegistry.reuseMobSpawn
import com.offz.spigot.mobzy.toTemplate
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Consumer

//TODO cleanup code after conversion to kotlin
class MobzyGUI(val player: Player) : HistoryGuiHolder(6, "Mobzy", mobzy) {
    private val mobConfigs: List<ClickableElement> = ArrayList()
    private val spawnList: List<ClickableElement> = ArrayList()
    private var config: FileConfiguration? = null

    private fun buildMobConfigLayout(): Layout {
        val layout = Layout()
        val grid = FillableElement(4, 8)
        layout.addElement(0, 0, grid)
        val configs: Collection<FileConfiguration> = mobzy.mobzyConfig.spawnCfgs.values
        if (configs.size == 1) return buildRegions(configs.first())
        else configs.forEach { config ->
            val name = if (config.contains("config.name")) config.getString("config.name")!! else "Unnamed configuration"
            val icon = if (config.contains("config.icon")) Material.getMaterial(config.getString("config.icon")!!)!! else Material.BEDROCK
            val cell = Cell.forMaterial(icon, name)
            val mobConfig = ClickableElement(cell) { setElement(buildRegions(config)) }
            grid.addElement(mobConfig)
        }
        addBackButton(layout)
        return layout
    }

    private fun buildRegions(config: FileConfiguration): Layout {
        this.config = config
        val layout = Layout()
        val grid = FillableElement(4, 8)
        layout.addElement(0, 0, grid)
        config.getMapList("regions").forEach(Consumer { region: Map<*, *> ->
            val regionName = region["name"] as String?
            var material: Material? = Material.BEDROCK
            if (region.containsKey("icon")) {
                val tempMaterial = Material.getMaterial((region["icon"] as String?)!!)
                if (tempMaterial != null) material = tempMaterial
            }
            val cell = Cell.forMaterial(material, regionName)
            val mobConfig = ClickableElement(cell) {
                @Suppress("UNCHECKED_CAST")
                setElement(buildSpawns(region["spawns"] as List<MutableMap<String, Any?>>, region["name"] as String))
            }
            grid.addElement(mobConfig)
        })
        addBackButton(layout)
        return layout
    }

    private fun buildSpawns(spawns: List<MutableMap<String, Any?>>, regionName: String): Layout {
        val layout = Layout()
        val grid = FillableElement(4, 8)
        layout.addElement(0, 0, grid)
        spawns.forEach { spawn ->
            val spawnBuilder =
                    if (spawn.containsKey("reuse"))
                        reuseMobSpawn(spawn["reuse"] as String).entityType.mobTemplate //TODO not sure if this is right because I changed it from before
                    else
                        (spawn["mob"] as String).toTemplate()
            val cell = Cell.forItemStack(spawnBuilder.modelItemStack)
            //open up the config layout with its menu options
            val mobConfig = ClickableElement(cell) { setElement(MobConfigLayout(this, spawn, regionName)) }
            grid.addElement(mobConfig)
        }
        addBackButton(layout)
        return layout
    }

    fun saveConfigValues(spawn: MutableMap<String, Any?>, mobProperties: List<MobzyPropertyElement>) {
        spawn.clear()
        mobProperties.forEach { spawn[it.key] = it.value }
        mobzy.mobzyConfig.saveSpawnCfg(config ?: error("Could not save config values, config was null"))
        player.sendMessage("${ChatColor.GREEN}Successfully saved mob's configuartion")
    }

    init {
        setElement(buildMobConfigLayout())
    }
}