package com.mineinabyss.mobzy.registration

import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.geary.ecs.types.GearyEntityTypes
import com.mineinabyss.mobzy.MobzyAddon
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.entity.typeName
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.mobzy
import org.bukkit.entity.Mob

object MobzyTypes : GearyEntityTypes<MobType>(mobzy) {
    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(type: NMSEntityType<*>): MobType = get(type.typeName)

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(entity: NMSEntity): MobType = get(entity.entityType)

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(customMob: CustomMob): MobType = get(customMob.entity.typeName)

    /** Gets a mob template if it is registered with the plugin, otherwise throws an [IllegalArgumentException] */
    operator fun get(entity: Mob): MobType = get(entity.typeName)

    fun registerTypes(mobzyAddon: MobzyAddon) {
        mobzyAddon.mobConfigDir.walk().filter { it.isFile }.forEach { file ->
            val name = file.nameWithoutExtension
            val type = Formats.yamlFormat.decodeFromString(MobType.serializer(), file.readText())
            MobzyTypeRegistry.registerMob(name, type)
            MobzyTypes.registerType(name, type)
        }
    }
}
