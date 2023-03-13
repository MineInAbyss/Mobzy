package com.mineinabyss.mobzy.pathfinding.bindings.goals

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.TemptGoal
import net.minecraft.world.item.crafting.Ingredient
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack
import org.bukkit.entity.Mob
import org.bukkit.inventory.ItemStack

@Serializable
@SerialName("minecraft:behavior.tempt")
class TemptBehavior(
    private val items: List<SerializableItemStack>,
    private val speed: Double = 1.0,
    private val losesInterest: Boolean = false
) : PathfinderComponent() {
    override fun build(mob: Mob) = TemptGoal(
        mob.toNMS<PathfinderMob>(),
        speed,
        items.map { it.toItemStack() }.toNMSRecipeItemStack(),
        losesInterest,
    )
}

fun Collection<ItemStack>.toNMSRecipeItemStack(): Ingredient =
    Ingredient.of(stream().map { CraftItemStack.asNMSCopy(it) })
