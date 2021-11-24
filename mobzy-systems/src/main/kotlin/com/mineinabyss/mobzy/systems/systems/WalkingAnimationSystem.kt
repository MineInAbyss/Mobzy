package com.mineinabyss.mobzy.systems.systems

import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import net.minecraft.world.entity.EnumItemSlot
import net.minecraft.world.phys.Vec3D
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack
import org.bukkit.entity.Mob

class WalkingAnimationSystem : TickingSystem(interval = 10) {
    private val ResultScope.model by get<Model>()
    private val ResultScope.mob by get<BukkitEntity>()

    override fun ResultScope.tick() {
        val mob = mob as? Mob ?: return

        val headItem = mob.toNMS().getEquipment(EnumItemSlot.f /* HEAD */)
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

//TODO move into idofront
val Vec3D.lengthSqr get() = x * x + y * y + z * z
