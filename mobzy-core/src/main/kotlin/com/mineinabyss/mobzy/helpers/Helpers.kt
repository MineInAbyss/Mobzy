package com.mineinabyss.mobzy.helpers

import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.helpers.entity
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.helpers.addPrefab
import com.mineinabyss.geary.prefabs.prefabs
import com.mineinabyss.idofront.typealiases.BukkitEntity
import org.bukkit.Location

fun Location.spawnFromPrefab(prefab: PrefabKey): BukkitEntity? {
    val entity = prefabs.manager[prefab] ?: return null
    return spawnFromPrefab(entity)
}

fun Location.spawnFromPrefab(prefab: GearyEntity): BukkitEntity? {
    return entity {
        addPrefab(prefab)
        set<Location>(this@spawnFromPrefab)
    }.get<BukkitEntity>()
}
