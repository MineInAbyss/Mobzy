package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import net.minecraft.server.v1_16_R2.NBTTagCompound

@AutoscanComponent
data class CopyNBT(
    val compound: NBTTagCompound
)
