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
import com.mineinabyss.mobzy.MobzyConfig
import com.mineinabyss.mobzy.configuration.SpawnConfig
import com.mineinabyss.mobzy.configuration.icon
import com.mineinabyss.mobzy.configuration.name
import com.mineinabyss.mobzy.configuration.regions
import com.mineinabyss.mobzy.gui.layouts.MobConfigLayout
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.mobzy.registration.MobTypes
import com.mineinabyss.mobzy.spawning.MobSpawn
import com.mineinabyss.mobzy.spawning.SpawnRegistry.findMobSpawn
import org.bukkit.entity.Player
import java.util.*

class MobzyGUI(val player: Player) : HistoryGuiHolder(6, "Mobzy", mobzy) {
    private val mobConfigs: List<ClickableElement> = ArrayList()
    private val spawnList: List<ClickableElement> = ArrayList()
    private var config: SpawnConfig? = null

    private fun buildMobConfigLayout(): Layout {
        val configs: Collection<SpawnConfig> = MobzyConfig.spawnCfgs
        return if (configs.size == 1)
            buildRegions(configs.first())
        else guiyLayout {
            setElement(0, 0, FillableElement(4, 8)) {
                for (config in configs) {
                    val name = config.name
                    val icon = config.icon
                    val cell = icon.toCell(name)
                    val mobConfig = ClickableElement(cell) { setElement(buildRegions(config)) }
                    addElement(mobConfig)
                }
            }
            addBackButton(this)
        }
    }

    private fun buildRegions(config: SpawnConfig): Layout = guiyLayout {
        this@MobzyGUI.config = config
        setElement(0, 0, FillableElement(4, 8)) {
            config.regions.forEach { region ->
                val regionName = region.name
                val material = region.icon

                button(material.toCell(regionName)) {
                    setElement(buildSpawns(region.spawns, region.name))
                }
            }
        }

        addBackButton(this)
    }

    private fun buildSpawns(spawns: List<MobSpawn>, regionName: String): Layout = guiyLayout {
        setElement(0, 0, FillableElement(4, 8)) {
            spawns.forEach { spawn ->
                val spawnBuilder = if (spawn.reuse != null)
                    MobTypes[findMobSpawn(spawn.reuse).entityTypeName ?: error("Reuse was null")]
                else
                    MobTypes[spawn.entityTypeName ?: error("Reuse was null")]

                //open up the config layout with its menu options
                button(spawnBuilder.modelItemStack.toCell()) {
                    setElement(MobConfigLayout(this@MobzyGUI, spawn))
                }
            }
        }

        addBackButton(this)
    }

    fun saveConfigValues() {
        config!!.save()
        player.success("Successfully saved mob's configuartion")
    }

    init {
        setElement(buildMobConfigLayout())
    }
}