package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R3.PathfinderGoalTempt
import net.minecraft.server.v1_16_R3.RecipeItemStack
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.entity.Creature
import org.bukkit.entity.Mob
import org.bukkit.inventory.ItemStack

@Serializable
@SerialName("minecraft:behavior.tempt")
class TemptBehavior(
    val items: List<Material>,
    val speed: Double = 1.0,
    val losesInterest: Boolean = false
) : PathfinderComponent() {
    override fun build(mob: Mob) = PathfinderGoalTempt(
        (mob as Creature).toNMS(),
        speed,
        losesInterest,
        items.map { ItemStack(it) }.toNMSRecipeItemStack()
    )

}

fun Collection<ItemStack>.toNMSRecipeItemStack(): RecipeItemStack =
    RecipeItemStack.a(this
        .map { CraftItemStack.asNMSCopy(it) }
        .stream())
