package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.aliases.NMSSnowball
import com.mineinabyss.mobzy.api.nms.aliases.NMSWorld
import com.mineinabyss.mobzy.ecs.components.initialization.ItemModel
import com.mineinabyss.mobzy.mobs.CustomEntity
import net.minecraft.server.v1_16_R2.EntitySnowball
import net.minecraft.server.v1_16_R2.EntityTypes
import net.minecraft.server.v1_16_R2.MovingObjectPosition
import net.minecraft.server.v1_16_R2.MovingObjectPositionEntity
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack

class ProjectileEntity(
    type: NMSEntityType<*>,
    world: NMSWorld
) : EntitySnowball(type as EntityTypes<EntitySnowball>, world), CustomEntity {
    override val gearyId: Int = Engine.getNextId()

    override val nmsEntity: NMSSnowball get() = this

    init {
        initEntity()
        item = CraftItemStack.asNMSCopy(get<ItemModel>()?.item?.toItemStack())
    }

    //Stop vanilla snowball hit behaviour
    override fun a(var0: MovingObjectPositionEntity) = Unit

    override fun a(var0: MovingObjectPosition) = Unit
}
