package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.idofront.nms.aliases.BukkitEntity
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import net.minecraft.server.v1_16_R2.EnumItemSlot
import net.minecraft.server.v1_16_R2.Vec3D
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack
import org.bukkit.entity.Mob

object WalkingAnimationSystem : TickingSystem(interval = 10) {
    private val model by get<Model>()
    private val mob by get<BukkitEntity>()

    override fun GearyEntity.tick() {
        val mob = mob as? Mob ?: return

        val headItem = mob.toNMS().getEquipment(EnumItemSlot.HEAD)
        val meta = CraftItemStack.getItemMeta(headItem) ?: return
        val modelId = meta.customModelData
        if (modelId != model.hitId) {
            if (mob.toNMS().mot.lengthSqr > 0.007) {
                if (modelId != model.walkId)
                    CraftItemStack.setItemMeta(headItem, meta.apply { setCustomModelData(model.walkId) })
            } else if (modelId != model.id)
                CraftItemStack.setItemMeta(headItem, meta.apply { setCustomModelData(model.id) })
        }
    }
}

//TODO move
val Vec3D.lengthSqr get() = x * x + y * y + z * z
