package com.mineinabyss.mobzy.api.nms.typeinjection

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mojang.datafixers.DataFixer
import net.minecraft.server.v1_16_R1.*

fun <T : NMSEntity> EntityTypes.Builder<T>.build(name: String): NMSEntityType<T> = a(name)

typealias NMSEntityTypeFactory<T> = EntityTypes.b<T>

typealias NMSEntityTypeBuilder = EntityTypes.Builder<Entity>

fun NMSEntityTypeBuilder.withSize(width: Float, height: Float) = this.a(width, height)

fun NMSEntityTypeBuilder.withoutSave() = this.b()

fun NMSEntityTypeBuilder.withFireImmunity() = this.c()

fun NMSEntityTypeFactory<Entity>.builderForType(creatureType: EnumCreatureType): EntityTypes.Builder<Entity>
        = EntityTypes.Builder.a(this, creatureType)

object NMSDataConverterRegistry {
    fun getDataFixer(): DataFixer = DataConverterRegistry.a()
}