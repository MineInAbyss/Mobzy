package com.mineinabyss.mobzy.gui

import com.derongan.minecraft.guiy.gui.ClickableElement
import com.derongan.minecraft.guiy.gui.FillableElement
import com.derongan.minecraft.guiy.gui.Layout
import com.derongan.minecraft.guiy.gui.layouts.HistoryGuiHolder
import com.derongan.minecraft.guiy.helpers.toCell
import com.derongan.minecraft.guiy.kotlin_dsl.button
import com.derongan.minecraft.guiy.kotlin_dsl.guiyLayout
import com.derongan.minecraft.guiy.kotlin_dsl.setElement
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.mobzy.gui.layouts.MobConfigLayout
import com.mineinabyss.mobzy.mobTemplate
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.mobzy.spawning.SpawnRegistry.reuseMobSpawn
import com.mineinabyss.mobzy.toTemplate
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.util.*

class MobzyGUI(val player: Player) : HistoryGuiHolder(6, "Mobzy", mobzy) {
    private val mobConfigs: List<ClickableElement> = ArrayList()
    private val spawnList: List<ClickableElement> = ArrayList()
    private var config: FileConfiguration? = null

    private fun buildMobConfigLayout(): Layout {
        val configs: Collection<FileConfiguration> = mobzy.mobzyConfig.spawnCfgs.values
        return if (configs.size == 1)
            buildRegions(configs.first())
        else guiyLayout {
            setElement(0, 0, FillableElement(4, 8)) {
                for (config in configs) {
                    val name = if (config.contains("config.name")) config.getString("config.name")!! else "Unnamed configuration"
                    val icon = if (config.contains("config.icon")) Material.getMaterial(config.getString("config.icon")!!)!! else Material.BEDROCK
                    val cell = icon.toCell(name)
                    val mobConfig = ClickableElement(cell) { setElement(buildRegions(config)) }
                    addElement(mobConfig)
                }
            }
            addBackButton(this)
        }
    }

    private fun buildRegions(config: FileConfiguration): Layout = guiyLayout {
        this@MobzyGUI.config = config
        setElement(0, 0, FillableElement(4, 8)) {
            config.getMapList("regions").forEach { region ->
                val regionName = region["name"] as String
                var material: Material = Material.BEDROCK
                if (region.containsKey("icon")) {
                    val tempMaterial = Material.getMaterial((region["icon"] as String?)!!)
                    if (tempMaterial != null) material = tempMaterial
                }

                button(material.toCell(regionName)) {
                    @Suppress("UNCHECKED_CAST")
                    setElement(buildSpawns(region["spawns"] as List<MutableMap<String, Any?>>, region["name"] as String))
                }
            }
        }

        addBackButton(this)
    }

    private fun buildSpawns(spawns: List<MutableMap<String, Any?>>, regionName: String): Layout =
            guiyLayout {
                setElement(0, 0, FillableElement(4, 8)) {
                    spawns.forEach { spawn ->
                        val spawnBuilder = if (spawn.containsKey("reuse"))
                            reuseMobSpawn(spawn["reuse"] as String).entityType.mobTemplate
                        else
                            (spawn["mob"] as String).toTemplate()

                        //open up the config layout with its menu options
                        button(spawnBuilder.modelItemStack.toCell()) {
                            setElement(MobConfigLayout(this@MobzyGUI, spawn, regionName))
                        }
                    }
                }

                addBackButton(this)
            }

    fun saveConfigValues(spawn: MutableMap<String, Any?>, mobProperties: List<Property>) {
        spawn.clear()
        mobProperties.forEach { spawn[it.key] = it.value }
        mobzy.mobzyConfig.saveSpawnCfg(config ?: error("Could not save config values, config was null"))
        player.success("Successfully saved mob's configuartion")
    }

    init {
        setElement(buildMobConfigLayout())
    }
}