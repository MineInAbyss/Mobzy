package com.mineinabyss.mobzy.injection

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.ecs.api.annotations.Handler
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.relations.Processed
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.api.systems.provideDelegate
import com.mineinabyss.geary.ecs.components.*
import com.mineinabyss.geary.ecs.query.Query
import com.mineinabyss.geary.papermc.globalContextMC
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.prefabs.configuration.components.Prefab
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.NMSWorld
import com.mineinabyss.idofront.nms.typeinjection.*
import com.mineinabyss.mobzy.ecs.components.initialization.MobzyType
import com.mineinabyss.mobzy.ecs.components.toMobzyCategory
import com.mineinabyss.mobzy.injection.types.*
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.DefaultAttributes
import net.minecraft.world.level.Level
import sun.misc.Unsafe
import java.lang.reflect.Field
import kotlin.collections.set

object MobzyTypesQuery : Query() {
    override fun onStart() {
        has<MobzyType>()
        has<Prefab>()
    }

    val TargetScope.key by get<PrefabKey>()
}

fun PrefabKey.toResourceKey(): ResourceLocation = ResourceLocation(namespace, name)

/**
 * @property types Used for getting a MobType from a String, which makes it easier to access from [MobType]
 * @property templates A map of mob [EntityTypes.mobName]s to [MobType]s.
 */
@Suppress("ObjectPropertyName")
@AutoScan
class MobzyNMSTypeInjector : GearyListener() {
    private val TargetScope.info by added<MobzyType>()
    private val TargetScope.key by added<PrefabKey>()

    override fun onStart() {
        target.has<Prefab>()
    }

    @Handler
    fun TargetScope.addNMSType() {
        val nmsEntityType = Registry.ENTITY_TYPE.getOptional(info.baseClass.toResourceKey()).orElseGet {
            error("Couldn't find a key ${info.baseClass} registered with minecraft.")
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

//    /**
//     * Registers a new entity with the server with extra parameters for width, height, and the function for creating the
//     * entity.
//     *
//     * @see injectType
//     */
//    fun inject(
//        key: PrefabKey,
//        prefabInfo: MobzyType,
//        attributes: MobAttributes = MobAttributes()
//    ): NMSEntityType<*> {
//        val init: (EntityType<NMSEntity>, Level) -> Entity =
//            (mobBaseClasses[prefabInfo.baseClass] as? (EntityType<NMSEntity>, Level) -> Entity
//                ?: error("Not a valid parent class: ${prefabInfo.baseClass}"))
//        val mobID = key.name.toEntityTypeName()
//        val injected: NMSEntityType<out NMSEntity> =
//            EntityType.Builder.of<NMSEntity>(EntityType.EntityFactory(init), prefabInfo.creatureType)
//                .sized(attributes.width, attributes.height)
//                .apply {
//                    if (attributes.fireImmune) fireImmune()
//                }
//                .injectType(namespace = key.namespace, key = mobID, extendFrom = "minecraft:zombie")
//
//        customAttributes[injected] = attributes.toNMSBuilder().build()
//        _types[mobID] = injected
//        return injected
//    }

    private val mobBaseClasses =
        mutableMapOf<String, (EntityType<out Nothing>, Level) -> Entity>(
            "mobzy:flying" to { type, world -> FlyingMob(type, world) }, //TODO use namespaced keys
            "mobzy:hostile" to { type, world -> HostileMob(type, world) },
            "mobzy:passive" to { type, world -> PassiveMob(type, world) },
            "mobzy:fish" to { type, world -> FishMob(type, world) },
            "mobzy:hostile_water" to { type, world -> HostileWaterMob(type, world) },
            "mobzy:npc" to { type, world -> NPC(type, world) },
        )

    fun addMobBaseClasses(vararg classes: Pair<String, (NMSEntityType<*>, NMSWorld) -> NMSEntity>) {
        mobBaseClasses += classes
    }
}

//TODO try to reduce usage around code, should really only be done in one central place
internal fun String.toEntityTypeName() = lowercase().replace(" ", "_")

fun NMSEntityType<*>.toPrefab(): GearyEntity? {
    return globalContextMC.prefabManager[globalNMSTypeInjector.getPrefabForType(this@toPrefab) ?: return null]
}
