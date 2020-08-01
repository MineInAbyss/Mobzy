package com.mineinabyss.mobzy.registration

import com.mineinabyss.mobzy.api.nms.aliases.NMSCreatureType
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.typeinjection.*
import com.mineinabyss.mobzy.mobs.MobTemplate
import net.minecraft.server.v1_16_R1.*
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * @property types Used for getting a MobType from a String, which makes it easier to access from [MobTemplate]
 * @property templates A map of mob [EntityTypes.mobName]s to [MobTemplate]s.
 */
@Suppress("ObjectPropertyName")
object MobzyTypes {
    val typeNames get() = _types.keys.toList()

    private val _types: MutableMap<String, EntityTypes<*>> = mutableMapOf()

    /** Gets a mob's [EntityTypes] from a String if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(name: String): EntityTypes<*> = _types[name.toEntityTypeName()]
            ?: error("Mob type ${name.toEntityTypeName()} not found, only know $typeNames")

    operator fun contains(name: String) = _types.contains(name.toEntityTypeName())

    private val customAttributes = mutableMapOf<EntityTypes<*>, AttributeProvider>()

    internal fun injectDefaultAttributes() {
        try {
            val modifiers: Field = Field::class.java.getDeclaredField("modifiers")
            modifiers.isAccessible = true
            val field: Field = AttributeDefaults::class.java.getDeclaredField("b")
            modifiers.setInt(field, modifiers.getInt(field) and Modifier.FINAL.inv())
            field.isAccessible = true

            @Suppress("UNCHECKED_CAST")
            val currentAttributes = HashMap(field.get(null) as Map<NMSEntityType<*>, NMSAttributeProvider>)
            currentAttributes += customAttributes
            field.set(null, currentAttributes)
        } catch (reason: Throwable) {
            error("Failed to inject custom attribute defaults")
        }
    }

    /**
     * Registers a new entity with the server with extra parameters for width, height, and the function for creating the
     * entity.
     *
     * @see injectNewEntity
     */
    fun registerMob(name: String, type: NMSCreatureType, width: Float, height: Float, init: (World) -> NMSEntity): EntityTypes<*> {
        val mobID = name.toEntityTypeName()
        val injected = (NMSEntityTypeFactory<Entity> { _, world -> init(world) })
                .builderForType(type)
//                .withFireImmunity()
                .withSize(width, height)
                .injectType(mobID, extendFrom = "zombie")

        customAttributes += injected to MobzyTemplates[injected].attributes.toNMSBuilder().build()
        _types[mobID] = injected
        return injected
    }
}

internal fun String.toEntityTypeName() = toLowerCase().replace(" ", "_")