@file:JvmMultifileClass
@file:JvmName("MobzyAPI")

package com.mineinabyss.mobzy.api

import com.mineinabyss.mobzy.api.nms.typeinjection.spawnEntity
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import org.bukkit.Location

fun Location.spawnMobzyMob(name: String) = spawnEntity(MobzyTypeRegistry[name])
