package com.mineinabyss.mobzy.api.nms.typeinjection

import com.mineinabyss.idofront.messaging.logWarn
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mojang.datafixers.DataFixUtils
import com.mojang.datafixers.types.Type
import net.minecraft.server.v1_16_R2.DataConverterTypes
import net.minecraft.server.v1_16_R2.Entity
import net.minecraft.server.v1_16_R2.IRegistry
import net.minecraft.server.v1_16_R2.SharedConstants

typealias NMSRegistry<T> = IRegistry<T>

/**
 * Registers an [NMSEntityType] with the server.
 */
fun <T : NMSEntity> NMSEntityType<T>.registerEntityType(key: String): NMSEntityType<T> =
    NMSRegistry.a(NMSRegistry.ENTITY_TYPE, key, this)

/**
 * Injects an entity into the server
 *
 * Originally from [paper forums](https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293)
 */
fun NMSEntityTypeBuilder.injectType(
    key: String,
    extendFrom: String
): NMSEntityType<Entity> { //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
    @Suppress("UNCHECKED_CAST") val dataTypes = NMSDataConverterRegistry.getDataFixer()
        .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().worldVersion))
        .findChoiceType(DataConverterTypes.ENTITY).types() as MutableMap<String, Type<*>>
    if (dataTypes.containsKey("minecraft:$key")) logWarn("ALREADY CONTAINS KEY: $key")
    dataTypes["minecraft:$key"] = dataTypes["minecraft:$extendFrom"]!!

    return build(key).registerEntityType(key)
}
