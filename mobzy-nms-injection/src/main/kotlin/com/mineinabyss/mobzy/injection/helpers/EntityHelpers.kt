package com.mineinabyss.mobzy.injection.helpers

import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.toBukkit

fun NMSEntity.toGeary(): GearyEntity = toBukkit().toGeary()
