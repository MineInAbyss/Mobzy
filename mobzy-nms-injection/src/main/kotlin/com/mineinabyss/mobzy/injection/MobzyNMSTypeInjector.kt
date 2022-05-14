package com.mineinabyss.mobzy.injection

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.components.Processed
import com.mineinabyss.geary.datatypes.family.family
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.configuration.components.Prefab
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.geary.systems.query.GearyQuery
import com.mineinabyss.mobzy.ecs.components.initialization.MobzyType
import com.mineinabyss.mobzy.ecs.components.toMobzyCategory
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation

object MobzyTypesQuery : GearyQuery() {
    val TargetScope.key by get<PrefabKey>()
    val TargetScope.isMobzy by family {
        has<MobzyType>()
        has<Prefab>()
    }

    fun getKeys() = MobzyTypesQuery.run { map { it.key } }
}

fun PrefabKey.toResourceKey(): ResourceLocation = ResourceLocation(namespace, key)

@AutoScan
class MobzyNMSTypeInjector : GearyListener() {
    private val TargetScope.info by added<MobzyType>()
    private val TargetScope.prefab by family { has<Prefab>() }

    @Handler
    fun TargetScope.addNMSType() {
        val nmsEntityType = Registry.ENTITY_TYPE.getOptional(info.baseClass.toResourceKey()).orElseGet {
            error("Couldn't find a key ${info.baseClass} registered with Minecraft.")
        }
        entity.set(nmsEntityType)
        entity.set(info.mobCategory ?: info.creatureType.toMobzyCategory())
        entity.setRelation(MobzyType::class, Processed)
    }
}
