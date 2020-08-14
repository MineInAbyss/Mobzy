package com.mineinabyss.mobzy.registration

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.entity.typeName
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.mobs.AnyCustomMob
import org.bukkit.entity.Mob

object MobTypes {
    private val types: MutableMap<String, MobType> = mutableMapOf()

    //TODO explanation
    private val hardcodedTypes: MutableMap<String, MobType> = mutableMapOf()

    operator fun get(name: String): MobType = types[name.toEntityTypeName()]
            ?: error("Mob template for $name not found")

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(type: NMSEntityType<*>): MobType = get(type.typeName)

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(customMob: AnyCustomMob): MobType = get(customMob.entity.typeName)

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(entity: Mob): MobType = get(entity.typeName)

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(entity: NMSEntity): MobType = get(entity.entityType)

    /** Gets the entity name from a [MobType] if registered, otherwise throws an [IllegalArgumentException]*/
    fun getNameForTemplate(type: MobType): String {
        return (types.entries.find { type === it.value }?.key
                ?: error("Template was accessed but not registered in any mob configuration"))
    }

    internal fun registerTemplates(templates: Map<String, MobType>) {
        this.types += templates
    }

    fun registerPersistentTemplate(mob: String, type: MobType) {
        val entityName = mob.toEntityTypeName()
        types[entityName] = type
        hardcodedTypes[entityName] = type
    }

    /** Clears all stored [types], but not [hardcodedTypes] */
    internal fun reset() {
        types.clear()
        types += hardcodedTypes
    }
}