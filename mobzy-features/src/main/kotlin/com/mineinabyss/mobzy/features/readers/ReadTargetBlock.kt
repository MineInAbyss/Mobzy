package com.mineinabyss.mobzy.features.readers

import com.mineinabyss.geary.annotations.optin.UnsafeAccessors
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.LivingEntity


@Serializable
@SerialName("mobzy:read.target_block")
class ReadTargetBlock(
    val maxDistance: Int,
)

@AutoScan
class ReadTargetBlockSystem : GearyListener() {
    private val Pointers.bukkit by get<BukkitEntity>().on(target)
    private val Pointers.read by get<ReadTargetBlock>().on(source)

    @OptIn(UnsafeAccessors::class)
    override fun Pointers.handle() {
        val targetBlock = (bukkit as? LivingEntity)?.getTargetBlock(null, read.maxDistance) ?: return
        event.entity.set(targetBlock.location)
    }
}
