package com.offz.spigot.mobzy

import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta


fun broadcast(message: String) = Bukkit.getConsoleSender().sendMessage(message)

fun ItemStack.editItemMeta(edits: (ItemMeta) -> Unit): ItemStack {
    val meta = this.itemMeta ?: return this
    edits(meta)
    this.itemMeta = meta
    return this
}

var ItemStack.damage: Int?
    get() = this.itemMeta?.damage
    set(value) {
        this.itemMeta?.damage = value!!
    }

var ItemMeta.damage
    get() = (this as Damageable).damage
    set(value) {
        if (this is Damageable) this.damage = value
    }