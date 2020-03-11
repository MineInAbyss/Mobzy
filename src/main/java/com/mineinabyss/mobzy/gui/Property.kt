package com.mineinabyss.mobzy.gui

import com.derongan.minecraft.guiy.gui.Cell
import com.derongan.minecraft.guiy.gui.ClickableElement
import com.derongan.minecraft.guiy.gui.Layout
import com.derongan.minecraft.guiy.gui.inputs.NumberInput
import com.derongan.minecraft.guiy.helpers.toCell
import com.derongan.minecraft.guiy.kotlin_dsl.backButtonTo
import com.derongan.minecraft.guiy.kotlin_dsl.button
import com.derongan.minecraft.guiy.kotlin_dsl.guiyLayout
import com.derongan.minecraft.guiy.kotlin_dsl.setElement
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.mobzy.gui.layouts.MobConfigLayout
import com.mineinabyss.mobzy.mobzy
import de.erethon.headlib.HeadLib
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.ChatColor
import org.bukkit.Material
import java.util.function.Consumer

/**
 * @param wrapped         Element to delegate all clicks to.
 * @param key             the key in the config that will be overridden when this element is saved
 * @param value           the value to be put into that key
 * @param mobConfigLayout
 */
class Property(
        private val wrapped: Cell,
        private val type: PropertyType,
        val key: String,
        var value: Any?,
        private val main: MobzyGUI,
        private val mobConfigLayout: MobConfigLayout
) : ClickableElement(wrapped) {

    private fun readString() {
        AnvilGUI.Builder().onClose { main.show(it) }
                .onComplete { _, text ->
                    //called when the inventory output slot is clicked
                    value = text
                    val item = wrapped.itemStack
                    item.editItemMeta { lore = listOf(text) }
                    AnvilGUI.Response.close()
                }
                .text(value.toString()) //sets the text the GUI should start with
                .plugin(mobzy) //set the plugin instance
                .open(main.player) //opens the GUI for the player provided
    }

    private fun readNumber(): Layout = guiyLayout {
        val numberInput = NumberInput() //TODO make setElement return the element created
        setElement(0, 0, numberInput) {
            submitAction = Consumer { value: Double ->
                this@Property.main.backInHistory()
                this@Property.wrapped.itemStack.editItemMeta {
                    if (this@Property.type == PropertyType.INTEGER_INPUT) {
                        this@Property.value = value.toInt()
                        lore = listOf(value.toInt().toString() + "")
                    } else if (this@Property.type == PropertyType.DOUBLE_INPUT) {
                        this@Property.value = value
                        lore = listOf(value.toString())
                    }
                }
            }
        }

        backButtonTo(this@Property.main)

        button(0, 5, (HeadLib.PLAIN_RED.toCell("${ChatColor.RED}Delete"))) {
            this@Property.mobConfigLayout.moveToUnused(this@Property)
            this@Property.main.backInHistory()
        }

        this@guiyLayout.button(4, 5, Material.DIAMOND_BLOCK.toCell("Submit Number")) {
            numberInput.onSubmit()
        }

    }

    enum class PropertyType {
        STRING_INPUT, INTEGER_INPUT, DOUBLE_INPUT
    }

    init {
        setClickActionKt {
            when {
                mobConfigLayout.unusedProperties.contains(this) -> mobConfigLayout.moveToUsed(this)
                type == PropertyType.INTEGER_INPUT || type == PropertyType.DOUBLE_INPUT -> main.setElement(readNumber())
                type == PropertyType.STRING_INPUT -> readString() //TODO Anvil GUI doesn't work
            }
        }
    }
}