package com.mineinabyss.mobzy.injection

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.datatypes.family.family
import com.mineinabyss.geary.components.Processed
import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.datatypes.family.MutableFamilyOperations.Companion.has
import com.mineinabyss.geary.papermc.globalContextMC
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.configuration.components.Prefab
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.geary.systems.accessors.get
import com.mineinabyss.geary.systems.query.GearyQuery
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.ecs.components.initialization.MobzyType
import com.mineinabyss.mobzy.ecs.components.toMobzyCategory
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.DefaultAttributes
import sun.misc.Unsafe
import java.lang.reflect.Field
import kotlin.collections.set

object MobzyTypesQuery : GearyQuery() {
    val TargetScope.key by get<PrefabKey>()
    val TargetScope.isMobzy by family {
        has<MobzyType>()
        has<Prefab>()
    }
}

fun PrefabKey.toResourceKey(): ResourceLocation = ResourceLocation(namespace, name)

@Suppress("ObjectPropertyName")
@AutoScan
class MobzyNMSTypeInjector : GearyListener() {
    private val TargetScope.info by added<MobzyType>()
    private val TargetScope.key by added<PrefabKey>()
    private val TargetScope.prefab by family { has<Prefab>() }

    @Handler
    fun TargetScope.addNMSType() {
        val nmsEntityType = Registry.ENTITY_TYPE.getOptional(info.baseClass.toResourceKey()).orElseGet {
            error("Couldn't find a key ${info.baseClass} registered with Minecraft.")
        }
//        val nmsEntityType = inject(key, info, entity.get() ?: MobAttributes())
        entity.set(nmsEntityType)
        entity.set(info.mobCategory ?: info.creatureType.toMobzyCategory())
        entity.setRelation(MobzyType::class, Processed)

        //TODO check id is still the same thing
        typeToPrefabMap[nmsEntityType.id] = key
    }

    val typeNames get() = _types.keys.toList()
    private val _types: MutableMap<String, NMSEntityType<*>> = mutableMapOf()
    private val typeToPrefabMap = mutableMapOf<String, PrefabKey>()

    fun getPrefabForType(nmsEntityType: NMSEntityType<*>): PrefabKey? =
        typeToPrefabMap[nmsEntityType.id]

    private val customAttributes = mutableMapOf<NMSEntityType<*>, AttributeSupplier>()

    fun clear() = customAttributes.clear()

    fun injectDefaultAttributes() {
        try {
            //TODO this just gets applied in LivingEntity's constructor, we could theoretically bypass reading
            // default attributes entirely by changing code there
            val attributeDefaultsField = DefaultAttributes::class.java.getDeclaredField("b")
            attributeDefaultsField.isAccessible = true

            @Suppress("UNCHECKED_CAST")
            val currentAttributes = attributeDefaultsField.get(null) as Map<NMSEntityType<*>, AttributeSupplier>

            val keyNamesToInject = customAttributes.map { it.key.id }

            val injected = currentAttributes
                // remove keys that are already injected
                .filterKeys { it.id !in keyNamesToInject } + customAttributes

            val unsafeField: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
            unsafeField.isAccessible = true
            val unsafe = unsafeField.get(null) as Unsafe
            val staticFieldBase = unsafe.staticFieldBase(attributeDefaultsField)
            val staticFieldOffset = unsafe.staticFieldOffset(attributeDefaultsField)
            unsafe.putObject(staticFieldBase, staticFieldOffset, injected)
        } catch (reason: Throwable) {
            reason.printStackTrace()
            error("Failed to inject custom attribute defaults")
        }
    }
}

//TODO try to reduce usage around code, should really only be done in one central place
internal fun String.toEntityTypeName() = lowercase().replace(" ", "_")

fun NMSEntityType<*>.toPrefab(): GearyEntity? {
    return globalContextMC.prefabManager[globalNMSTypeInjector.getPrefabForType(this@toPrefab) ?: return null]
}
