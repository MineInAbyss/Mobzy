package com.mineinabyss.mobzy.registration

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.aliases.NMSWorld
import com.mineinabyss.mobzy.api.nms.typeinjection.*
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.mobs.types.*
import com.mineinabyss.mobzy.mobs.types.NPC
import net.minecraft.server.v1_16_R2.*
import sun.misc.Unsafe
import java.lang.reflect.Field

/**
 * @property types Used for getting a MobType from a String, which makes it easier to access from [MobType]
 * @property templates A map of mob [EntityTypes.mobName]s to [MobType]s.
 */
@Suppress("ObjectPropertyName")
object MobzyTypeRegistry {
    val typeNames get() = _types.keys.toList()

    private val _types: MutableMap<String, NMSEntityType<*>> = mutableMapOf()

    /** Gets a mob's [EntityTypes] from a String if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(name: String): NMSEntityType<*> = _types[name.toEntityTypeName()]
            ?: error("Mob type ${name.toEntityTypeName()} not found, only know $typeNames")

    operator fun contains(name: String) = _types.contains(name.toEntityTypeName())

    private val customAttributes = mutableMapOf<NMSEntityType<*>, AttributeProvider>()

    internal fun injectDefaultAttributes() {
        try {
            val attributeDefaultsField = AttributeDefaults::class.java.getDeclaredField("b")
            attributeDefaultsField.isAccessible = true

            @Suppress("UNCHECKED_CAST")
            val currentAttributes = HashMap(attributeDefaultsField.get(null) as Map<NMSEntityType<*>, NMSAttributeProvider>)
            currentAttributes += customAttributes

            val unsafeField: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
            unsafeField.isAccessible = true
            val unsafe = unsafeField.get(null) as Unsafe
            val staticFieldBase = unsafe.staticFieldBase(attributeDefaultsField)
            val staticFieldOffset = unsafe.staticFieldOffset(attributeDefaultsField)
            unsafe.putObject(staticFieldBase, staticFieldOffset, currentAttributes)
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
    fun registerMob(name: String, type: MobType): EntityTypes<*> {
        val init = mobBaseClasses[type.baseClass] ?: error("Not a valid parent class: ${type.baseClass}")
        val mobID = name.toEntityTypeName()
        val attributes = type.get<MobAttributes>() ?: MobAttributes()
        val injected: NMSEntityType<Entity> = (NMSEntityTypeFactory<Entity> { entityType, world -> init(entityType, world) })
                .builderForCreatureType(type.creatureType)
                .withSize(attributes.width, attributes.height)
                .apply {
                    if (attributes.fireImmune) withFireImmunity()
                }
                .injectType(mobID, extendFrom = "zombie")

        customAttributes += injected to attributes.toNMSBuilder().build()
        _types[mobID] = injected
        return injected
    }

    private val mobBaseClasses = mutableMapOf<String, (NMSEntityType<*>, NMSWorld) -> NMSEntity>(
            "mobzy:flying" to ::FlyingMob, //TODO use proper keys
            "mobzy:hostile" to ::HostileMob,
            "mobzy:passive" to ::PassiveMob,
            "mobzy:fish" to ::FishMob,
            "mobzy:npc" to ::NPC
    )

    fun addMobBaseClasses(vararg classes: Pair<String, (NMSEntityType<*>, NMSWorld) -> NMSEntity>) {
        mobBaseClasses += classes
    }
}

//TODO try to reduce usage around code, should really only be done in one central place
internal fun String.toEntityTypeName() = toLowerCase().replace(" ", "_")
