package com.mineinabyss.mobzy.registration

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.components.*
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.NMSWorld
import com.mineinabyss.idofront.nms.entity.keyName
import com.mineinabyss.idofront.nms.typeinjection.*
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.ecs.components.initialization.MobzyTypeInjectionComponent
import com.mineinabyss.mobzy.mobs.types.*
import com.mineinabyss.mobzy.mobs.types.NPC
import net.minecraft.server.v1_16_R2.*
import sun.misc.Unsafe
import java.lang.reflect.Field
import kotlin.collections.set

/**
 * @property types Used for getting a MobType from a String, which makes it easier to access from [MobType]
 * @property templates A map of mob [EntityTypes.mobName]s to [MobType]s.
 */
@Suppress("ObjectPropertyName")
object MobzyNMSTypeInjector : TickingSystem() {
    private val info by get<MobzyTypeInjectionComponent>()
    private val key by get<PrefabKey>()

    override fun GearyEntity.tick() {
        set(inject(key.name, info, get<MobAttributes>() ?: MobAttributes()))
        //TODO concurrent modification exception
        remove<MobzyTypeInjectionComponent>()
    }

    val typeNames get() = _types.keys.toList()
    private val _types: MutableMap<String, NMSEntityType<*>> = mutableMapOf()

    private val customAttributes = mutableMapOf<NMSEntityType<*>, AttributeProvider>()

    internal fun clear() = customAttributes.clear()

    internal fun injectDefaultAttributes() {
        try {
            val attributeDefaultsField = AttributeDefaults::class.java.getDeclaredField("b")
            attributeDefaultsField.isAccessible = true

            @Suppress("UNCHECKED_CAST")
            val currentAttributes =
                (attributeDefaultsField.get(null) as Map<NMSEntityType<*>, NMSAttributeProvider>)

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
        name: String,
        prefabInfo: MobzyTypeInjectionComponent,
        attributes: MobAttributes = MobAttributes()
    ): EntityTypes<*> {
        val init = mobBaseClasses[prefabInfo.baseClass] ?: error("Not a valid parent class: ${prefabInfo.baseClass}")
        val mobID = name.toEntityTypeName()
        val injected: NMSEntityType<Entity> =
            (NMSEntityTypeFactory<Entity> { entityType, world -> init(entityType, world) })
                .builderForCreatureType(prefabInfo.creatureType)
                .withSize(attributes.width, attributes.height)
                .apply {
                    if (attributes.fireImmune) withFireImmunity()
                }
                .injectType(mobID, extendFrom = "zombie")

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
internal fun String.toEntityTypeName() = toLowerCase().replace(" ", "_")
