@file:JvmMultifileClass
@file:JvmName("MobzyAPI")

package com.mineinabyss.mobzy.api

import com.mineinabyss.mobzy.api.nms.aliases.NMSCreatureType
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSWorld
import com.mineinabyss.mobzy.mobs.MobTemplate
import com.mineinabyss.mobzy.registration.MobzyTemplates
import com.mineinabyss.mobzy.registration.MobzyTypes
import com.mineinabyss.mobzy.registration.spawnEntity
import org.bukkit.Location

fun Location.spawnMobzyMob(name: String) = spawnEntity(MobzyTypes[name])

fun registerPersistentTemplate(mob: String, template: MobTemplate): MobTemplate {
    MobzyTemplates.registerPersistentTemplate(mob, template)
    return template
}

fun registerMob(
        name: String,
        creatureType: NMSCreatureType,
        width: Float = 1f,
        height: Float = 2f,
        init: (NMSWorld) -> NMSEntity
) = MobzyTypes.registerMob(name, creatureType, width, height, init)