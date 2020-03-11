package com.mineinabyss.mobzy.gui.layouts

import com.derongan.minecraft.guiy.gui.Cell
import com.derongan.minecraft.guiy.gui.FillableElement
import com.derongan.minecraft.guiy.gui.Layout
import com.derongan.minecraft.guiy.gui.ScrollingPallet
import com.derongan.minecraft.guiy.helpers.toCell
import com.derongan.minecraft.guiy.kotlin_dsl.backButtonTo
import com.derongan.minecraft.guiy.kotlin_dsl.button
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.mobzy.gui.MobzyGUI
import com.mineinabyss.mobzy.gui.Property
import com.mineinabyss.mobzy.spawning.MobSpawn
import de.erethon.headlib.HeadLib
import net.minecraft.server.v1_15_R1.EntityTypes

private typealias PT = Property.PropertyType
private typealias HL = HeadLib

class MobConfigLayout(private val main: MobzyGUI, val spawn: MutableMap<String, Any?>, val regionName: String) : Layout() {
    private val _mobProperties: MutableList<Property> = mutableListOf()
    private val _unusedProperties: MutableList<Property> = mutableListOf()
    private val grid = FillableElement(4, 9)
    private val scrollingPallet = ScrollingPallet(9)

    val unusedProperties: List<Property> get() = _unusedProperties.toList()
    val mobProperties: List<Property> get() = mobProperties.toList()

    init {
        makeMobOptions()
        setElement(0, 0, grid)
        setElement(0, 4, scrollingPallet)
        reloadProperties()

        button(4, 5, HL.CHECKMARK.toCell("Save")) {
            main.saveConfigValues(spawn, _mobProperties)
        }

        backButtonTo(main)
    }

    fun moveToUnused(property: Property) {
        _mobProperties.remove(property)
        _unusedProperties.add(property)
        reloadProperties()
    }

    fun moveToUsed(property: Property) {
        _unusedProperties.remove(property)
        _mobProperties.add(property)
        reloadProperties()
    }

    private fun reloadProperties() {
        grid.clear()
        grid.addAll(_mobProperties)
        scrollingPallet.clear()
        scrollingPallet.addAll(_unusedProperties)
    }


    private fun makeMobOptions() {
        with(MobSpawn(EntityTypes.ZOMBIE)) {
            makeProperty(HL.QUARTZ_R, PT.STRING_INPUT, "reuse", "reuse")
            makeProperty(HL.QUARTZ_M, PT.STRING_INPUT, "mob", "ENITTY_TYPE_HERE")
            makeProperty(HL.QUARTZ_P, PT.DOUBLE_INPUT, "priority", basePriority)
            makeProperty(HL.STONE_A, PT.DOUBLE_INPUT, "min-amount", minAmount)
            makeProperty(HL.WOODEN_A, PT.DOUBLE_INPUT, "max-amount", maxAmount)
            makeProperty(HL.STONE_G, PT.DOUBLE_INPUT, "min-gap", minGap)
            makeProperty(HL.WOODEN_G, PT.DOUBLE_INPUT, "max-gap", maxGap)
            makeProperty(HL.OBJECT_BLUE_LANTERN, PT.DOUBLE_INPUT, "min-light", minLight)
            makeProperty(HL.OBJECT_LIT_BLUE_LANTERN, PT.DOUBLE_INPUT, "max-light", maxLight)
            makeProperty(HL.PLAIN_DARK_YELLOW, PT.DOUBLE_INPUT, "min-time", minTime)
            makeProperty(HL.PLAIN_YELLOW, PT.DOUBLE_INPUT, "max-time", maxTime)
            makeProperty(HL.STONE_Y, PT.DOUBLE_INPUT, "min-y", minY)
            makeProperty(HL.WOODEN_Y, PT.DOUBLE_INPUT, "max-y", maxY)
            makeProperty(HL.QUARTZ_R, PT.DOUBLE_INPUT, "radius", radius)
            makeProperty(HL.PLAIN_GRASS_GREEN, PT.DOUBLE_INPUT, "spawn-pos", spawnPos)
            makeProperty(HL.PLAIN_WHITE, PT.DOUBLE_INPUT, "block-whitelist", blockWhitelist)
        }
    }

    private fun makeProperty(head: HL, type: PT, key: String, default: Any): Property =
            if (spawn.containsKey(key))
                makePropertyFromValue(head, type, key, spawn[key]).also { _mobProperties.add(it) }
            else
                makePropertyFromValue(head, type, key, default).also { _unusedProperties.add(it) }

    private fun makePropertyFromValue(head: HL, type: PT, key: String, value: Any?): Property =
            Property(itemTemplate(head, key, value.toString()), type, key, value, main, this)

    private fun itemTemplate(head: HL, name: String, lore: String): Cell =
            head.toItemStack().editItemMeta {
                setDisplayName(name)
                this.lore = listOf(lore)
            }.toCell() as Cell
}