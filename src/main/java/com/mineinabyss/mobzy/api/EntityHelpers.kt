package com.mineinabyss.mobzy.api

import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.nms.entity.typeName
import com.mineinabyss.mobzy.mobs.CustomEntity
import org.bukkit.entity.Entity

/** Whether an entity is a renamed mob registered with Mobzy. */
//TODO feels like an unnecessarily specific function?
val Entity.isCustomAndRenamed get() = if (!extendsCustomClass || customName == null) false else customName != this.typeName

/** Whether this is a custom entity registered with Mobzy. */
val Entity.extendsCustomClass get() = toNMS().extendsCustomClass

/** Whether this is a custom entity registered with Mobzy. */
val NMSEntity.extendsCustomClass get() = this is CustomEntity

