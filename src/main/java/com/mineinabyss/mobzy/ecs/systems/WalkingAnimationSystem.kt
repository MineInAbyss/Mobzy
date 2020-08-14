package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.Model
import com.mineinabyss.mobzy.ecs.components.minecraft.EntityComponent
import net.minecraft.server.v1_16_R1.EnumItemSlot
import net.minecraft.server.v1_16_R1.Vec3D
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack

object WalkingAnimationSystem : TickingSystem(interval = 10) {
    override fun tick() = Engine.runFor<Model, EntityComponent> { model, (mob) ->
        val headItem = mob.nmsEntity.getEquipment(EnumItemSlot.HEAD)
        val meta = CraftItemStack.getItemMeta(headItem)
        val modelId = meta.customModelData
        if (modelId != model.hitId) {
            if (mob.nmsEntity.mot.lengthSqr > 0.007) {
                if (modelId != model.walkId)
                    CraftItemStack.setItemMeta(headItem, meta.apply { setCustomModelData(model.walkId) })
            } else if (modelId != model.id)
                CraftItemStack.setItemMeta(headItem, meta.apply { setCustomModelData(model.id) })
        }
    }

    private val Vec3D.lengthSqr get() = x * x + y * y + z * z
}