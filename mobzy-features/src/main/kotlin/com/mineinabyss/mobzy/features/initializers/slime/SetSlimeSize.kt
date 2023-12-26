package com.mineinabyss.mobzy.features.initializers.slime

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
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
    private val Pointers.slimeSize by get<SetSlimeSize>().whenSetOnTarget()
    private val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()

    override fun Pointers.handle() {
        val slime = (bukkit as? Slime) ?: return
        slime.size = slimeSize.size.randomOrMin()
    }
}
