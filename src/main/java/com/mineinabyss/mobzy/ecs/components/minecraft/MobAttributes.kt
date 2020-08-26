package com.mineinabyss.mobzy.ecs.components.minecraft

import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.api.nms.typeinjection.NMSAttributeBuilder
import com.mineinabyss.mobzy.api.nms.typeinjection.NMSAttributes
import com.mineinabyss.mobzy.api.nms.typeinjection.set
import com.mineinabyss.mobzy.ecs.components.get
import com.mineinabyss.mobzy.mobs.CustomMob
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R2.GenericAttributes

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
) : MobzyComponent {

    fun toNMSBuilder(): NMSAttributeBuilder = NMSAttributes.forEntityInsentient()
            .set(GenericAttributes.ARMOR, armor)
            .set(GenericAttributes.ARMOR_TOUGHNESS, armorToughness)
            .set(GenericAttributes.ATTACK_DAMAGE, attackDamage)
            .set(GenericAttributes.ATTACK_KNOCKBACK, attackKnockback)
            .set(GenericAttributes.ATTACK_SPEED, attackSpeed)
            .set(GenericAttributes.FLYING_SPEED, flyingSpeed)
            .set(GenericAttributes.FOLLOW_RANGE, followRange)
            .set(GenericAttributes.JUMP_STRENGTH, jumpStrength)
            .set(GenericAttributes.KNOCKBACK_RESISTANCE, knockbackResistance)
            .set(GenericAttributes.LUCK, luck)
            .set(GenericAttributes.MAX_HEALTH, maxHealth)
            .set(GenericAttributes.MOVEMENT_SPEED, movementSpeed)
            .set(GenericAttributes.SPAWN_REINFORCEMENTS, spawnReinforcements)
}

val CustomMob.attributes get() = get<MobAttributes>()