package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import net.minecraft.world.entity.EntityDimensions
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity

//TODO Make sure this is the first thing to run when priority support comes
@AutoScan
class AddPrefabFromNMSTypeSystem : GearyListener() {
    private val TargetScope.bukkitEntity by onSet<BukkitEntity>()
    private val TargetScope.attributes by onSet<MobAttributes>()

    @Handler
    fun TargetScope.addPrefab() {
        val dimensions = NMSEntity::class.java.declaredFields.first { it.type == EntityDimensions::class.java }
        val nms = bukkitEntity.toNMS()
        dimensions.isAccessible = true
        val fixed = (dimensions.get(nms) as EntityDimensions).fixed
        dimensions.set(nms, EntityDimensions(attributes.width, attributes.height, fixed))

        // Set attributes
        val living: LivingEntity = bukkitEntity as? LivingEntity ?: return
        fun setAttribute(type: Attribute, baseValue: Double?) {
            if (baseValue == null) return
            living.registerAttribute(type)
            living.getAttribute(type)?.baseValue = baseValue
        }

        with(attributes) {
            setAttribute(Attribute.GENERIC_MAX_HEALTH, maxHealth)
            maxHealth?.let { living.health = it }
            setAttribute(Attribute.GENERIC_FOLLOW_RANGE, followRange)
            setAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE, knockbackResistance)
            setAttribute(Attribute.GENERIC_MOVEMENT_SPEED, movementSpeed)
            setAttribute(Attribute.GENERIC_FLYING_SPEED, flyingSpeed)
            setAttribute(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage)
            setAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK, attackKnockback)
            setAttribute(Attribute.GENERIC_ATTACK_SPEED, attackSpeed)
            setAttribute(Attribute.GENERIC_ARMOR, armor)
            setAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS, armorToughness)
            setAttribute(Attribute.GENERIC_LUCK, luck)
            setAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS, spawnReinforcements)
            setAttribute(Attribute.HORSE_JUMP_STRENGTH, jumpStrength)
        }
    }
}
