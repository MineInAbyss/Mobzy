package com.mineinabyss.mobzy.pathfinders.hostile

import com.mineinabyss.mobzy.mobs.CustomMob
import net.minecraft.server.v1_15_R1.EntityProjectileThrowable
import net.minecraft.server.v1_15_R1.EntitySnowball
import org.bukkit.inventory.ItemStack

/**
 * @property itemToThrow The item to be thrown at the target. Will set the item of the thrown projectile. Use if you
 * just want a regular snowball with a different looking item thrown.
 */
interface ItemThrowable : CustomMob {
    val itemToThrow: ItemStack? get() = null

    /** @return A new entity that will be spawned and thrown at the player. Defaults to a snowball. **/
    fun createThrownEntity(): EntityProjectileThrowable = EntitySnowball(entity.world, entity)
}