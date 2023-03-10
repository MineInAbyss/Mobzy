package com.mineinabyss.mobzy.injection

import com.mineinabyss.geary.datatypes.family.family
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.configuration.components.Prefab
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.geary.systems.query.GearyQuery
import com.mineinabyss.geary.papermc.tracking.entities.components.SetEntityType
import net.minecraft.resources.ResourceLocation

object MobzyTypesQuery : GearyQuery() {
    val TargetScope.key by get<PrefabKey>()
    val TargetScope.isMobzy by family {
        has<SetEntityType>()
        has<Prefab>()
    }

    fun getKeys() = MobzyTypesQuery.run { map { it.key } }
}

fun PrefabKey.toResourceKey(): ResourceLocation = ResourceLocation(namespace, key)
