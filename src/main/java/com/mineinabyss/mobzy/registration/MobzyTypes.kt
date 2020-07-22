package com.mineinabyss.mobzy.registration

import com.mineinabyss.mobzy.mobs.MobTemplate
import net.minecraft.server.v1_16_R1.Entity
import net.minecraft.server.v1_16_R1.EntityTypes
import net.minecraft.server.v1_16_R1.EnumCreatureType
import net.minecraft.server.v1_16_R1.World
import kotlin.collections.set

/**
 * @property types Used for getting a MobType from a String, which makes it easier to access from [MobTemplate]
 * @property templates A map of mob [EntityTypes.mobName]s to [MobTemplate]s.
 */
@Suppress("ObjectPropertyName")
object MobzyTypes {
    val typeNames get() = _types.keys.toList()

    private val _types: MutableMap<String, EntityTypes<*>> = mutableMapOf()

    /** Gets a mob's [EntityTypes] from a String if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(name: String): EntityTypes<*> = _types[name.toEntityTypeName()] ?: error("Mob type ${name.toEntityTypeName()} not found, only know $typeNames")

    operator fun contains(name: String) = _types.contains(name.toEntityTypeName())

    /**
     * Registers a new entity with the server with extra parameters for width, height, and the function for creating the
     * entity.
     *
     * @see injectNewEntity
     */
    fun registerEntity(name: String, type: EnumCreatureType, width: Float, height: Float, func: (World) -> Entity): EntityTypes<*> {
        val mobID = name.toEntityTypeName()
        val injected: EntityTypes<*> = injectNewEntity(mobID, "zombie", bToa(EntityTypes.b { _, world -> func(world) }, type).c().a(width, height))
        _types[mobID] = injected
        return injected
    }
}

internal fun String.toEntityTypeName() = toLowerCase().replace(" ", "_")