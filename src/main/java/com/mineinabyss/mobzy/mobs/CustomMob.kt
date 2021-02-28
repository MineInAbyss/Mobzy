package com.mineinabyss.mobzy.mobs

import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.mobzy.api.nms.aliases.*
import com.mineinabyss.mobzy.api.nms.player.addKillScore
import com.mineinabyss.mobzy.ecs.components.death.DeathLoot
import com.mineinabyss.mobzy.mobs.types.MobBase
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_16_R2.event.CraftEventFactory
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Mob

/**
 * A class for all our custom living entities to extend. Some shared behaviour is in here, though because we often
 * need to access protected variables, we force implementation of them here. Since a lot of these things have obfuscated
 * names, we may expand upon this class to act as a wrapper interface of sorts for custom NMS mobs.
 *
 * We share how we override some functions to implement custom behaviour with the help of [MobBase] and an
 * annotation processor.
 *
 * @see MobBase
 *
 * @property killScore The score with which a player should be rewarded with when the current entity is killed.
 */
interface CustomMob : CustomEntity {

    override val nmsEntity: NMSEntityInsentient
    override val entity: Mob

    val killScore: Int

    /** A function to implement pathfinders that should be added to all entities of this type. */
    fun createPathfinders()

    /** Drops the correct amount of EXP from death at this entity's location. */
    fun dropExp()

    /** Called when a player interacts with this entity. */
    fun onPlayerInteract(player: HumanEntity, enumhand: NMSHand): NMSInteractionResult

    /** Custom logic for what happens when this entity dies. Override the NMS die method with this. */
    fun dieCustom(damageSource: NMSDamageSource?) {
        val nmsWorld: NMSWorld = entity.world.toNMS()
        if (!nmsEntity.dead) {
            nmsEntity.dead = true
            val killer = nmsEntity.killer
            if (killScore >= 0 && killer != null) killer.addKillScore(nmsEntity, killScore, damageSource)
            // this line causes the entity to send a statistics update on death (we don't want this as it causes a NPE exception and crash)
//            killer?.a_(nmsEntity);

            if (entity.isSleeping) nmsEntity.entityWakeup()

            if (!nmsEntity.world.isClientSide) {
                if (nmsWorld.gameRules.getBoolean(NMSGameRules.DO_MOB_LOOT) && killer != null) {
                    dropItems(killer.bukkitEntity)
                } else CraftEventFactory.callEntityDeathEvent(nmsEntity)
            }
            nmsEntity.combatTracker.g() //resets combat tracker

            nmsWorld.broadcastEntityEffect(nmsEntity, 3.toByte())
            nmsEntity.pose = NMSEntityPose.DYING
            //TODO add PlaceHolderAPI support
            geary(entity).get<DeathLoot>()?.deathCommands?.forEach {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), it)
            }
        }
    }

    /** Custom logic for spawning item drops (upon this entity's death.) */
    fun dropItems(killer: HumanEntity) {
        val heldItem = killer.inventory.itemInMainHand
        val looting = heldItem.enchantments[Enchantment.LOOT_BONUS_MOBS] ?: 0
        val fire = heldItem.enchantments[Enchantment.FIRE_ASPECT] ?: 0 > 0
        geary(entity).with<DeathLoot> { deathLoot ->
            CraftEventFactory.callEntityDeathEvent(
                nmsEntity,
                deathLoot.drops.toList().map { it.chooseDrop(looting, fire) })
            deathLoot.expToDrop()?.let { nmsEntity.expToDrop = it }
        }
        dropExp()
    }
}
