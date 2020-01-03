package com.offz.spigot.abyssalcreatures.mobs.hostile

import com.offz.spigot.mobzy.editItemMeta
import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.HostileMob
import net.minecraft.server.v1_15_R1.World
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

class Tamaugachi(world: World?) : HostileMob(world, "Tamaugachi"), HitBehaviour {
    init {
        //make them walk fast in water thanks to depth strider
        living.equipment!!.boots = ItemStack(Material.LEATHER_BOOTS).editItemMeta {
            it.isUnbreakable = true
            it.addEnchant(Enchantment.DEPTH_STRIDER, 40, true)
        }
    }
}