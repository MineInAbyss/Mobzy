@file:JvmMultifileClass
@file:JvmName("MobzyAPI")

package com.mineinabyss.mobzy.api

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.nms.entity.typeName
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.registration.toEntityTypeName
import org.bukkit.entity.Entity

/** Whether an entity is a renamed mob registered with Mobzy. */
val Entity.isRenamed get() = if (!isCustomMob || customName == null) false else customName != this.typeName

/** Converts [Entity] to [CustomMob]. */
fun Entity.toMobzy() = toNMS().toMobzy()

/** Converts [NMSEntity] to [CustomMob]. */
fun NMSEntity.toMobzy() = this as? CustomMob

/** @return Whether the mob is of type of the given [typeName]. */
fun Entity.isOfType(typeName: String) = this.typeName == typeName.toEntityTypeName()

/** Whether this is a custom mob registered with Mobzy. */
val Entity.isCustomMob get() = toNMS().isCustomMob

/** Whether this is a custom mob registered with Mobzy. */
val NMSEntity.isCustomMob get() = this is CustomMob
