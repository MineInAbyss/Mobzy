package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.TemptGoal
import net.minecraft.world.item.crafting.Ingredient
import org.bukkit.Material
import org.bukkit.entity.Creature
import org.bukkit.inventory.ItemStack

@Serializable
@SerialName("minecraft:behavior.tempt")
class TemptBehavior(
    val items: List<Material>,
    val speed: Double = 1.0,
    val losesInterest: Boolean = false
) : PathfinderComponent() {
    override fun build(mob: Creature) = TemptGoal(
        mob.toNMS(),
        speed,
        items.map { ItemStack(it) }.toNMSRecipeItemStack(),
        losesInterest,
    )
}

fun Collection<ItemStack>.toNMSRecipeItemStack(): Ingredient =
    Ingredient.of(stream().map { it.toNMS() })
