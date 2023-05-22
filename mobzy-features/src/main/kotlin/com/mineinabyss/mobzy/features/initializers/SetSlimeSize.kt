package com.mineinabyss.mobzy.features.initializers

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.idofront.util.randomOrMin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Slime

@JvmInline
@Serializable
@SerialName("mobzy:set.slime_size")
value class SetSlimeSize(@Serializable(with = IntRangeSerializer::class) val size: IntRange)

@AutoScan
class SetSlimeSizeSystem : GearyListener() {
    private val TargetScope.slimeSize by onSet<SetSlimeSize>()
    private val TargetScope.bukkit by onSet<BukkitEntity>()

    @Handler
    fun TargetScope.apply() {
        val slime = (bukkit as? Slime) ?: return
        slime.size = slimeSize.size.randomOrMin()
    }
}
