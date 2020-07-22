@file:JvmMultifileClass
@file:JvmName("MobzyAPI")

package com.mineinabyss.mobzy.api

import com.mineinabyss.mobzy.registration.MobzyTypes
import net.minecraft.server.v1_16_R1.EntityTypes
import org.bukkit.entity.Entity

fun Entity.toEntityTypesViaScoreboardTags(): EntityTypes<*> = MobzyTypes[scoreboardTags.first { MobzyTypes.contains(it) }]

/** The name of the mob type as registered in Minecraft, ex. `entity.minecraft.zombie`. */
val EntityTypes<*>.keyName: String get() = this.f()

/** The mob's [EntityTypes.keyName] without the `entity.minecraft.` prefix */
val EntityTypes<*>.typeName: String get() = this.keyName.removePrefix("entity.minecraft.")