package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.idofront.nms.aliases.NMSDataContainer

@AutoscanComponent
data class CopyNBT(
    val compound: NMSDataContainer
)
