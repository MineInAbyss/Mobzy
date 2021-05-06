package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.idofront.nms.typeinjection.NMSAttributeBuilder
import com.mineinabyss.idofront.nms.typeinjection.NMSAttributes
import com.mineinabyss.idofront.nms.typeinjection.NMSGenericAttributes
import com.mineinabyss.idofront.nms.typeinjection.set
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:attributes")
@AutoscanComponent
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
        .set(NMSGenericAttributes.ARMOR, armor)
        .set(NMSGenericAttributes.ARMOR_TOUGHNESS, armorToughness)
        .set(NMSGenericAttributes.ATTACK_DAMAGE, attackDamage)
        .set(NMSGenericAttributes.ATTACK_KNOCKBACK, attackKnockback)
        .set(NMSGenericAttributes.ATTACK_SPEED, attackSpeed)
        .set(NMSGenericAttributes.FLYING_SPEED, flyingSpeed)
        .set(NMSGenericAttributes.FOLLOW_RANGE, followRange)
        .set(NMSGenericAttributes.JUMP_STRENGTH, jumpStrength)
        .set(NMSGenericAttributes.KNOCKBACK_RESISTANCE, knockbackResistance)
        .set(NMSGenericAttributes.LUCK, luck)
        .set(NMSGenericAttributes.MAX_HEALTH, maxHealth)
        .set(NMSGenericAttributes.MOVEMENT_SPEED, movementSpeed)
        .set(NMSGenericAttributes.SPAWN_REINFORCEMENTS, spawnReinforcements)
}
