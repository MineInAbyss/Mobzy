package com.mineinabyss.mobzy.registration

import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.mobzy.api.typeName
import com.mineinabyss.mobzy.mobs.MobTemplate
import com.mineinabyss.mobzy.mobzyConfig
import net.minecraft.server.v1_15_R1.EntityTypes

object MobzyTemplates {
    private var templates: Map<String, MobTemplate> = mapOf()

    //TODO explanation
    private val hardCodedTemplates: MutableMap<String, MobTemplate> = mutableMapOf()

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(name: String): MobTemplate = templates[name.toEntityTypeName()]
            ?: error("Mob template for $name not found")

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(type: EntityTypes<*>): MobTemplate = templates[type.typeName.toEntityTypeName()]
            ?: error("Mob template for ${type.typeName} not found")

    /** Gets the entity name from a [MobTemplate] if registered, otherwise throws an [IllegalArgumentException]*/
    fun getNameForTemplate(template: MobTemplate): String {
        return (templates.entries.find { template === it.value }?.key
                ?: error("Template was accessed but not registered in any mob configuration"))
    }

    /**
     * Registers the mob attributes for each item in [types] by getting their associated template from config.
     * We end up with an updated [templates] list for reading from later.
     *
     * @see readTemplateConfig
     */
    fun loadTemplatesFromConfig() {
        templates = hardCodedTemplates.plus(readTemplateConfig())
        logSuccess("Registered templates for: ${templates.keys}")
    }

    /** Deserializes the templates for all mobs in the configuration */
    private fun readTemplateConfig(): Map<String, MobTemplate> {
        val map = mutableMapOf<String, MobTemplate>()
        mobzyConfig.mobCfgs.forEach {
            map += it.info.templates
        }
        return map.toMap()
    }

    fun registerHardCodedTemplate(mob: String, template: MobTemplate) = hardCodedTemplates.put(mob.toEntityTypeName(), template)

    /** Clears all stored [templates], but not [hardCodedTemplates] */
    internal fun clear() {
        templates = mapOf()
    }
}