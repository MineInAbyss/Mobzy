package com.mineinabyss.mobzy.registration

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.entity.typeName
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.registration.MobTypes.persistentTypes
import com.mineinabyss.mobzy.registration.MobTypes.types
import org.bukkit.entity.Mob

/**
 * Manages registered [MobType]s and accessing them via name, bukkit entity, etc...
 *
 * @property types A map of [MobType]s registered with the plugin.
 * @property persistentTypes A map of types similar to [types], which persists (command) reloads. Useful for other
 * plugins which register types via code and can't re-register them easily.
 */
object MobTypes {
    private val types: MutableMap<String, MobType> = mutableMapOf()
    private val persistentTypes: MutableMap<String, MobType> = mutableMapOf()

    operator fun get(name: String): MobType = types[name.toEntityTypeName()]
            ?: error("Mob template for $name not found")

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(type: NMSEntityType<*>): MobType = get(type.typeName)

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(customMob: CustomMob): MobType = get(customMob.entity.typeName)

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(entity: Mob): MobType = get(entity.typeName)

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(entity: NMSEntity): MobType = get(entity.entityType)

    /** Gets the entity name from a [MobType] if registered, otherwise throws an [IllegalArgumentException]*/
    fun getNameForTemplate(type: MobType): String {
        return (types.entries.find { type === it.value }?.key
                ?: error("Template was accessed but not registered in any mob configuration"))
    }

    /** Registers [MobType]s with the plugin. These will be cleared after a command reload is triggered. */
    internal fun registerTypes(types: Map<String, MobType>) {
        this.types += types
    }

    /** Registers persistent [MobType]s with the plugin which do not get cleared after a command reload is triggered. */
    fun registerPersistentType(mob: String, type: MobType) {
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