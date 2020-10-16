@file:JvmMultifileClass
@file:JvmName("MobzyAPI")

package com.mineinabyss.mobzy.api

import com.mineinabyss.mobzy.api.nms.typeinjection.spawnEntity
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import com.mineinabyss.mobzy.registration.MobzyTypes
import org.bukkit.Location

fun Location.spawnMobzyMob(name: String) = spawnEntity(MobzyTypeRegistry[name])

fun registerPersistentTemplate(mob: String, type: MobType): MobType {
    MobzyTypes.registerPersistentType(mob, type)
    return type
}