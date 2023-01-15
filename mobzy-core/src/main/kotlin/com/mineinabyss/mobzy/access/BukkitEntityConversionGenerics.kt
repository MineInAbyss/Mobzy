package com.mineinabyss.mobzy.access

import com.mineinabyss.geary.datatypes.GearyEntity
import org.bukkit.entity.Entity

//Split into separate files with BukkitEntityComponent for Java interoperability reasons.

inline fun <reified T : Entity> GearyEntity.toBukkit(): T? =
    get<Entity>() as? T
