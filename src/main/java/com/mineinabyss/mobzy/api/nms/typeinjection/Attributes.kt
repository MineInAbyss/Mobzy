package com.mineinabyss.mobzy.api.nms.typeinjection

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityInsentient
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityLiving
import net.minecraft.server.v1_16_R1.AttributeBase
import net.minecraft.server.v1_16_R1.AttributeProvider

typealias NMSAttributeProvider = AttributeProvider
typealias NMSAttributeBuilder = AttributeProvider.Builder

object NMSAttributes {
    fun emptyBuilder(): NMSAttributeBuilder = NMSAttributeProvider.a()
    fun forEntityLiving(): NMSAttributeBuilder = NMSEntityLiving.cK()
    fun forEntityInsentient(): NMSAttributeBuilder = NMSEntityInsentient.p()
}

fun NMSAttributeBuilder.set(attribute: AttributeBase, value: Double? = null): NMSAttributeBuilder {
    if (value != null) a(attribute, value)
    return this
}// else a(attribute)

fun NMSAttributeBuilder.build(): NMSAttributeProvider = a()