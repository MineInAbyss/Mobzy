@file:JvmMultifileClass
@file:JvmName("MobzyAPI")

package com.mineinabyss.mobzy.api

import com.mineinabyss.mobzy.api.nms.typeinjection.spawnEntity
import com.mineinabyss.mobzy.ecs.components.AnyMobType
import com.mineinabyss.mobzy.registration.MobTypes
import com.mineinabyss.mobzy.registration.MobzyRegistry
import org.bukkit.Location

fun Location.spawnMobzyMob(name: String) = spawnEntity(MobzyRegistry[name])

fun registerPersistentTemplate(mob: String, type: AnyMobType): AnyMobType {
    MobTypes.registerPersistentTemplate(mob, type)
    return type
}