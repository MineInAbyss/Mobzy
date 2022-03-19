package com.mineinabyss.mobzy.ecs.components.initialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes

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
    fun AttributeSupplier.Builder.addNullable(attribute: Attribute, value: Double?): AttributeSupplier.Builder {
        if (value != null) add(attribute, value)
        return this
    }

    fun toNMSBuilder(): AttributeSupplier.Builder = AttributeSupplier.builder()
        .addNullable(Attributes.MAX_HEALTH, maxHealth)
        .addNullable(Attributes.FOLLOW_RANGE, followRange)
        .addNullable(Attributes.KNOCKBACK_RESISTANCE, knockbackResistance)
        .addNullable(Attributes.MOVEMENT_SPEED, movementSpeed)
        .addNullable(Attributes.FLYING_SPEED, flyingSpeed)
        .addNullable(Attributes.ATTACK_DAMAGE, attackDamage)
        .addNullable(Attributes.ATTACK_KNOCKBACK, attackKnockback)
        .addNullable(Attributes.ATTACK_SPEED, attackSpeed)
        .addNullable(Attributes.ARMOR, armor)
        .addNullable(Attributes.ARMOR_TOUGHNESS, armorToughness)
        .addNullable(Attributes.LUCK, luck)
        .addNullable(Attributes.SPAWN_REINFORCEMENTS_CHANCE, spawnReinforcements)
        .addNullable(Attributes.JUMP_STRENGTH, jumpStrength)
}
