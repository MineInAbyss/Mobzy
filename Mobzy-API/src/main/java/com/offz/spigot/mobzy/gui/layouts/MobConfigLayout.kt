package com.offz.spigot.mobzy.gui.layouts

import com.derongan.minecraft.guiy.gui.*
import com.offz.spigot.mobzy.gui.MobzyGUI
import com.offz.spigot.mobzy.gui.MobzyPropertyElement
import com.offz.spigot.mobzy.gui.MobzyPropertyElement.PropertyType
import com.offz.spigot.mobzy.spawning.MobSpawn
import de.erethon.headlib.HeadLib
import net.minecraft.server.v1_15_R1.EntityTypes
import java.util.function.Consumer

class MobConfigLayout(private val main: MobzyGUI, val spawn: MutableMap<String, Any?>, val regionName: String) : Layout() {
    private val mobProperties: MutableList<MobzyPropertyElement> = mutableListOf()
    private val unusedProperties: MutableList<MobzyPropertyElement> = mutableListOf()
    private val grid = FillableElement(4, 8)
    private val scrollingPallet = ScrollingPallet(9)

    fun getMobProperties(): List<MobzyPropertyElement> {
        return mobProperties
    }

    fun getUnusedProperties(): List<MobzyPropertyElement> {
        return unusedProperties
    }

    fun moveToUnused(property: MobzyPropertyElement) {
        mobProperties.remove(property)
        unusedProperties.add(property)
        reloadProperties()
    }

    fun moveToUsed(property: MobzyPropertyElement) {
        unusedProperties.remove(property)
        mobProperties.add(property)
        reloadProperties()
    }

    private fun reloadProperties() {
        grid.clear()
        mobProperties.forEach(Consumer { element: MobzyPropertyElement? -> grid.addElement(element) })
        scrollingPallet.clear()
        unusedProperties.forEach(Consumer { cell: MobzyPropertyElement? -> scrollingPallet.addTool(cell) })
    }

    private fun makeMobOptions() {
        val s = MobSpawn(EntityTypes.ZOMBIE) //getting default values from here

        makeProperty(HeadLib.QUARTZ_R, PropertyType.STRING_INPUT, "reuse", "reuse")
        makeProperty(HeadLib.QUARTZ_M, PropertyType.STRING_INPUT, "mob", "ENITTY_TYPE_HERE")
        makeProperty(HeadLib.QUARTZ_P, PropertyType.DOUBLE_INPUT, "priority", s.basePriority)
        makeProperty(HeadLib.STONE_A, PropertyType.DOUBLE_INPUT, "min-amount", s.minAmount)
        makeProperty(HeadLib.WOODEN_A, PropertyType.DOUBLE_INPUT, "max-amount", s.maxAmount)
        makeProperty(HeadLib.STONE_G, PropertyType.DOUBLE_INPUT, "min-gap", s.minGap)
        makeProperty(HeadLib.WOODEN_G, PropertyType.DOUBLE_INPUT, "max-gap", s.maxGap)
        makeProperty(HeadLib.OBJECT_BLUE_LANTERN, PropertyType.DOUBLE_INPUT, "min-light", s.minLight)
        makeProperty(HeadLib.OBJECT_LIT_BLUE_LANTERN, PropertyType.DOUBLE_INPUT, "max-light", s.maxLight)
        makeProperty(HeadLib.PLAIN_DARK_YELLOW, PropertyType.DOUBLE_INPUT, "min-time", s.minTime)
        makeProperty(HeadLib.PLAIN_YELLOW, PropertyType.DOUBLE_INPUT, "max-time", s.maxTime)
        makeProperty(HeadLib.STONE_Y, PropertyType.DOUBLE_INPUT, "min-y", s.minY)
        makeProperty(HeadLib.WOODEN_Y, PropertyType.DOUBLE_INPUT, "max-y", s.maxY)
        makeProperty(HeadLib.QUARTZ_R, PropertyType.DOUBLE_INPUT, "radius", s.radius)
        makeProperty(HeadLib.PLAIN_GRASS_GREEN, PropertyType.DOUBLE_INPUT, "spawn-pos", s.spawnPos)
        makeProperty(HeadLib.PLAIN_WHITE, PropertyType.DOUBLE_INPUT, "block-whitelist", s.blockWhitelist)
    }

    private fun makeProperty(head: HeadLib, type: PropertyType, key: String, default: Any): MobzyPropertyElement =
            if (spawn.containsKey(key))
                makePropertyFromValue(head, type, key, spawn[key]).also { mobProperties.add(it) }
            else makePropertyFromValue(head, type, key, default).also { unusedProperties.add(it) }

    private fun makePropertyFromValue(head: HeadLib, type: PropertyType, key: String, value: Any?): MobzyPropertyElement =
            MobzyPropertyElement(itemTemplate(head, key, value.toString()), type, key, value, main, spawn, this)

    companion object {
        fun itemTemplate(head: HeadLib, name: String?, lore: String): Cell {
            val item = head.toItemStack()
            val meta = item.itemMeta
            meta!!.setDisplayName(name)
            meta.lore = listOf(lore)
            item.itemMeta = meta
            return Cell.forItemStack(item) as Cell
        }
    }

    init {
        makeMobOptions()
        addElement(0, 0, grid)
        mobProperties.forEach { grid.addElement(it) }
        addElement(0, 4, scrollingPallet)
        unusedProperties.forEach(Consumer { cell: MobzyPropertyElement? -> scrollingPallet.addTool(cell) })
        val save = ClickableElement(Cell.forItemStack(HeadLib.CHECKMARK.toItemStack(), "Save"))
        { main.saveConfigValues(spawn, mobProperties) }
        addElement(4, 5, save)
        main.addBackButton(this)
    }
}