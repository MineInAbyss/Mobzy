package com.mineinabyss.mobzy.registration

import com.mineinabyss.geary.ecs.prefab.GearyPrefab
import com.mineinabyss.geary.ecs.prefab.PrefabManager
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.entity.typeName
import com.mineinabyss.mobzy.mobs.CustomEntity
import org.bukkit.entity.Mob

/** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
operator fun PrefabManager.get(type: NMSEntityType<*>): GearyPrefab? = get(type.typeName)

/** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
operator fun PrefabManager.get(entity: NMSEntity): GearyPrefab? = get(entity.entityType)

/** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
operator fun PrefabManager.get(customEntity: CustomEntity): GearyPrefab? = get(customEntity.entity.typeName)

/** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
operator fun PrefabManager.get(entity: Mob): GearyPrefab? = get(entity.typeName)

