package com.mineinabyss.geary.ecs.types

import com.mineinabyss.mobzy.registration.toEntityTypeName

abstract class GearyEntityTypes<T : GearyEntityType> {
    private val types: MutableMap<String, T> = mutableMapOf()
    private val persistentTypes: MutableMap<String, T> = mutableMapOf()

    operator fun get(name: String): T = types[name.toEntityTypeName()]
            ?: error("Mob template for $name not found")

    /** Gets the entity name from a type [T] if registered, otherwise throws an [IllegalArgumentException]*/
    fun getNameForTemplate(type: GearyEntityType): String =
            (types.entries.find { type === it.value }?.key
                    ?: error("Template was accessed but not registered in any configuration"))

    /** Registers entity types with the plugin. These will be cleared after a command reload is triggered. */
    internal fun registerType(name: String, type: T) {
        types[name] = type
    }

    /** Registers entity types with the plugin. These will be cleared after a command reload is triggered. */
    internal fun registerTypes(types: Map<String, T>) {
        this.types += types
    }

    /** Registers persistent entity types with the plugin which do not get cleared after a command reload is triggered. */
    fun registerPersistentType(mob: String, type: T) {
        val entityName = mob.toEntityTypeName()
        types[entityName] = type
        persistentTypes[entityName] = type
    }

    /** Clears all stored [types], but not [persistentTypes] */
    internal fun reset() {
        types.clear()
        types += persistentTypes
    }
}