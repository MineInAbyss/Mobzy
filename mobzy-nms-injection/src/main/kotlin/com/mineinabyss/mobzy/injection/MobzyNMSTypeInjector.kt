package com.mineinabyss.mobzy.injection

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.relations.Processed
import com.mineinabyss.geary.ecs.api.systems.ComponentAddSystem
import com.mineinabyss.geary.ecs.components.*
import com.mineinabyss.geary.ecs.engine.iteration.QueryResult
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.ecs.prefab.PrefabManager
import com.mineinabyss.geary.ecs.query.Query
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.NMSWorld
import com.mineinabyss.idofront.nms.entity.keyName
import com.mineinabyss.idofront.nms.typeinjection.*
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.ecs.components.initialization.MobzyType
import com.mineinabyss.mobzy.ecs.components.toMobCategory
import com.mineinabyss.mobzy.injection.types.*
import net.minecraft.world.entity.ai.attributes.AttributeDefaults
import net.minecraft.world.entity.ai.attributes.AttributeProvider
import sun.misc.Unsafe
import java.lang.reflect.Field
import kotlin.collections.set

object MobzyTypesQuery : Query({
    has<MobzyType>()
    has<Prefab>()
}) {
    val QueryResult.key by get<PrefabKey>()
}

/**
 * @property types Used for getting a MobType from a String, which makes it easier to access from [MobType]
 * @property templates A map of mob [EntityTypes.mobName]s to [MobType]s.
 */
@Suppress("ObjectPropertyName")
object MobzyNMSTypeInjector : ComponentAddSystem() {
    private val GearyEntity.info by get<MobzyType>()
    private val GearyEntity.key by get<PrefabKey>()

    init {
        has<Prefab>()
    }

    override fun GearyEntity.run() {
        val nmsEntityType = inject(key, info, get() ?: MobAttributes())
        set(nmsEntityType)
        set(info.mobCategory ?: info.creatureType.toMobCategory())
        setRelation<Processed, MobzyType>(Processed, data = true)

        typeToPrefabMap[nmsEntityType.keyName] = key
    }

    val typeNames get() = _types.keys.toList()
    private val _types: MutableMap<String, NMSEntityType<*>> = mutableMapOf()
    private val typeToPrefabMap = mutableMapOf<String, PrefabKey>()

    fun getPrefabForType(nmsEntityType: NMSEntityType<*>): PrefabKey? =
        typeToPrefabMap[nmsEntityType.keyName]

    private val customAttributes = mutableMapOf<NMSEntityType<*>, AttributeProvider>()

    fun clear() = customAttributes.clear()

    fun injectDefaultAttributes() {
        try {
            val attributeDefaultsField = AttributeDefaults::class.java.getDeclaredField("b")
            attributeDefaultsField.isAccessible = true

            @Suppress("UNCHECKED_CAST")
            val currentAttributes = attributeDefaultsField.get(null) as Map<NMSEntityType<*>, NMSAttributeProvider>

            val keyNamesToInject = customAttributes.map { it.key.keyName }

            val injected = currentAttributes
                // remove keys that are already injected
                .filterKeys { it.keyName !in keyNamesToInject } + customAttributes

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

    /**
     * Registers a new entity with the server with extra parameters for width, height, and the function for creating the
     * entity.
     *
     * @see injectType
     */
    fun inject(
        key: PrefabKey,
        prefabInfo: MobzyType,
        attributes: MobAttributes = MobAttributes()
    ): NMSEntityType<*> {
        val init = mobBaseClasses[prefabInfo.baseClass] ?: error("Not a valid parent class: ${prefabInfo.baseClass}")
        val mobID = key.name.toEntityTypeName()
        val injected: NMSEntityType<NMSEntity> =
            (NMSEntityTypeFactory<NMSEntity> { entityType, world -> init(entityType, world) })
                .builderForCreatureType(prefabInfo.creatureType)
                .withSize(attributes.width, attributes.height)
                .apply {
                    if (attributes.fireImmune) withFireImmunity()
                }
                .injectType(namespace = key.namespace, key = mobID, extendFrom = "minecraft:zombie")

        customAttributes[injected] = attributes.toNMSBuilder().build()
        _types[mobID] = injected
        return injected
    }

    private val mobBaseClasses = mutableMapOf<String, (NMSEntityType<*>, NMSWorld) -> NMSEntity>(
        "mobzy:flying" to ::FlyingMob, //TODO use namespaced keys
        "mobzy:hostile" to ::HostileMob,
        "mobzy:passive" to ::PassiveMob,
        "mobzy:fish" to ::FishMob,
        "mobzy:npc" to ::NPC,
    )

    fun addMobBaseClasses(vararg classes: Pair<String, (NMSEntityType<*>, NMSWorld) -> NMSEntity>) {
        mobBaseClasses += classes
    }
}

//TODO try to reduce usage around code, should really only be done in one central place
internal fun String.toEntityTypeName() = lowercase().replace(" ", "_")

fun NMSEntityType<*>.toPrefab(): GearyEntity? {
    return PrefabManager[MobzyNMSTypeInjector.getPrefabForType(this) ?: return null]
}