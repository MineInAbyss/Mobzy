package com.mineinabyss.mobzy.features.drops

import com.mineinabyss.geary.papermc.bridge.events.entities.Drops
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.spawning.spawn
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.idofront.util.randomOrMin
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageEvent.DamageCause

class DropsSystem : GearyListener() {
    private val Pointers.drops by get<Drops>().orNull().on(event)
    private val Pointers.bukkit by get<BukkitEntity>().on(target)
    private val Pointers.deathLoot by get<DoDrop>().on(source)

    override fun Pointers.handle() {
        val bukkit = bukkit
        val drops = drops

        // Drop equipped items from rideable entity
//        if(entity is Saddleable) drops.add(ItemStack(Material.SADDLE))
        val ignoredCauses = mutableListOf<DamageCause>()
        deathLoot.drops.forEach { mobDrop ->
            ignoredCauses.addAll(mobDrop.ignoredCauses)
        }
        val exp = deathLoot.drops.mapNotNull { it.exp?.randomOrMin() }.sum()
        if (bukkit.lastDamageCause?.cause !in ignoredCauses) {
            drops?.items?.clear()
            drops?.exp = 0
            val looting = (bukkit as? LivingEntity)
                ?.killer?.inventory?.itemInMainHand
                ?.enchantments?.get(Enchantment.LOOT_BONUS_MOBS) ?: 0
            val newDrops = deathLoot.drops.mapNotNull { it.chooseDrop(looting, bukkit.fireTicks > 0) }
            newDrops.forEach {
                bukkit.location.spawn<Item> { itemStack = it }
            }
            bukkit.location.spawn<ExperienceOrb>() {
                experience = exp
            }
        }
    }
}
