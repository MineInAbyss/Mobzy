package com.mineinabyss.mobzy.systems.systems

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import net.minecraft.world.entity.EnumItemSlot
import net.minecraft.world.phys.Vec3D
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack
import org.bukkit.entity.Mob
import kotlin.time.Duration.Companion.seconds

@AutoScan
class WalkingAnimationSystem : TickingSystem(interval = 0.5.seconds) {
    private val TargetScope.model by get<Model>()
    private val TargetScope.mob by get<BukkitEntity>()

    override fun TargetScope.tick() {
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
