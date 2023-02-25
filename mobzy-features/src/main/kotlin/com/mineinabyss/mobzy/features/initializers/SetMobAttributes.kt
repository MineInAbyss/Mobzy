package com.mineinabyss.mobzy.features.initializers

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.EntityDimensions
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity

@Serializable
@SerialName("mobzy:set.attributes")
data class SetMobAttributes(
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
}

@AutoScan
class SetMobAttributesSystem : GearyListener() {
    private val TargetScope.bukkitEntity by onSet<BukkitEntity>()
    private val TargetScope.attributes by onSet<SetMobAttributes>()


    @Handler
    fun TargetScope.setAttributes() {
        bukkitEntity.toNMS().setDimensions(attributes.width, attributes.height)

        // Set attributes
        val living: LivingEntity = bukkitEntity as? LivingEntity
            ?: error("Tried to set attributes of non living entity $bukkitEntity")

        fun set(type: Attribute, baseValue: Double?) {
            if (baseValue == null) return
            living.registerAttribute(type)
            living.getAttribute(type)?.baseValue = baseValue
        }

        with(attributes) {
            maxHealth?.let { living.health = it }
            set(Attribute.GENERIC_MAX_HEALTH, maxHealth)
            set(Attribute.GENERIC_FOLLOW_RANGE, followRange)
            set(Attribute.GENERIC_KNOCKBACK_RESISTANCE, knockbackResistance)
            set(Attribute.GENERIC_MOVEMENT_SPEED, movementSpeed)
            set(Attribute.GENERIC_FLYING_SPEED, flyingSpeed)
            set(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage)
            set(Attribute.GENERIC_ATTACK_KNOCKBACK, attackKnockback)
            set(Attribute.GENERIC_ATTACK_SPEED, attackSpeed)
            set(Attribute.GENERIC_ARMOR, armor)
            set(Attribute.GENERIC_ARMOR_TOUGHNESS, armorToughness)
            set(Attribute.GENERIC_LUCK, luck)
            set(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS, spawnReinforcements)
            set(Attribute.HORSE_JUMP_STRENGTH, jumpStrength)
        }
    }

    private fun NMSEntity.setDimensions(width: Float, height: Float) {
        val dimensions = NMSEntity::class.java.declaredFields.first { it.type == EntityDimensions::class.java }
        dimensions.isAccessible = true
        val fixed = (dimensions.get(this) as EntityDimensions).fixed
        dimensions.set(this, EntityDimensions(width, height, fixed))
    }
}
