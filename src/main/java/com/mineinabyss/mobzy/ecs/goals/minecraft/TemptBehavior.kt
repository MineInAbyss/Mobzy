package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.NMSEntityCreature
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.PathfinderGoalTempt
import net.minecraft.world.item.crafting.RecipeItemStack
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack
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
        mob.toNMS<NMSEntityCreature>(),
        speed,
        items.map { ItemStack(it) }.toNMSRecipeItemStack(),
        losesInterest,
    )
}

fun Collection<ItemStack>.toNMSRecipeItemStack(): RecipeItemStack =
    RecipeItemStack.a(this
        .map { CraftItemStack.asNMSCopy(it) }
        .stream())
