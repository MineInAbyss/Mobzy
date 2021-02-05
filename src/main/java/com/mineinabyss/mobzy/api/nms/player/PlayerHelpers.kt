package com.mineinabyss.mobzy.api.nms.player

import com.mineinabyss.mobzy.api.nms.aliases.NMSDamageSource
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityHuman

fun NMSEntityHuman.addKillScore(entity: NMSEntity, score: Int, damageSource: NMSDamageSource?) {
    a(entity, score, damageSource)
}
