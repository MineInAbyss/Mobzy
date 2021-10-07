package com.mineinabyss.mobzy

import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.idofront.serialization.SerializablePrefabItemService
import com.mineinabyss.looty.LootyFactory
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack

@Serializable
object MobzySerializablePrefabItemService : SerializablePrefabItemService {
    override fun prefabToItem(prefabName: String): ItemStack? =
        LootyFactory.createFromPrefab(PrefabKey.of(prefabName))
}
