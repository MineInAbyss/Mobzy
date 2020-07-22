package com.mineinabyss.mobzy.registration

import com.mineinabyss.mobzy.api.nms.aliases.BukkitEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.entity.typeName
import com.mineinabyss.mobzy.configuration.templates
import com.mineinabyss.mobzy.mobs.MobTemplate

object MobzyTemplates {
    private val templates: MutableMap<String, MobTemplate> = mutableMapOf()

    //TODO explanation
    private val persistentTemplates: MutableMap<String, MobTemplate> = mutableMapOf()

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(name: String): MobTemplate = templates[name.toEntityTypeName()]
            ?: error("Mob template for $name not found")

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(type: NMSEntityType<*>): MobTemplate = MobzyTemplates[type.typeName]

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(entity: BukkitEntity): MobTemplate = MobzyTemplates[entity.typeName]

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(entity: NMSEntity): MobTemplate = MobzyTemplates[entity.entityType.typeName]

    /** Gets the entity name from a [MobTemplate] if registered, otherwise throws an [IllegalArgumentException]*/
    fun getNameForTemplate(template: MobTemplate): String {
        return (templates.entries.find { template === it.value }?.key
                ?: error("Template was accessed but not registered in any mob configuration"))
    }

    internal fun registerTemplates(templates: Map<String, MobTemplate>){
        this.templates += templates
    }

    fun registerPersistentTemplate(mob: String, template: MobTemplate) {
        val entityName = mob.toEntityTypeName()
        templates[entityName] = template
        persistentTemplates[entityName] = template
    }

    /** Clears all stored [templates], but not [persistentTemplates] */
    internal fun reset() {
        templates.clear()
        templates += persistentTemplates
    }
}