package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.idofront.nms.typeinjection.NMSAttributeBuilder
import com.mineinabyss.idofront.nms.typeinjection.NMSAttributes
import com.mineinabyss.idofront.nms.typeinjection.NMSGenericAttributes
import com.mineinabyss.idofront.nms.typeinjection.set
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:attributes")
data class MobAttributes(
    val width: Float = 0.7f,
    val height: Float = 0.7f,
    val fireImmune: Boolean = false,
    val armor: Double? = null,
    val armorToughness: Double? = null,
    val attackDamage: Double? = null,
    val attackKnockback: Double? = null,
    val attackSpeed: Double? = null,
    val flyingSpeed: Double? = null,
    val followRange: Double? = null,
    val jumpStrength: Double? = null,
    val knockbackResistance: Double? = null,
    val luck: Double? = null,
    val maxHealth: Double? = null,
    val movementSpeed: Double? = 0.25,
    val spawnReinforcements: Double? = null
) {

    fun toNMSBuilder(): NMSAttributeBuilder = NMSAttributes.forEntityInsentient()
        .set(NMSGenericAttributes.a, maxHealth)
        .set(NMSGenericAttributes.b, followRange)
        .set(NMSGenericAttributes.c, knockbackResistance)
        .set(NMSGenericAttributes.d, movementSpeed)
        .set(NMSGenericAttributes.e, flyingSpeed)
        .set(NMSGenericAttributes.f, attackDamage)
        .set(NMSGenericAttributes.g, attackKnockback)
        .set(NMSGenericAttributes.h, attackSpeed)
        .set(NMSGenericAttributes.i, armor)
        .set(NMSGenericAttributes.j, armorToughness)
        .set(NMSGenericAttributes.k, luck)
        .set(NMSGenericAttributes.l, spawnReinforcements)
        .set(NMSGenericAttributes.m, jumpStrength)
}
