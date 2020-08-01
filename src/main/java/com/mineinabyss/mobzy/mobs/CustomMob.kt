package com.mineinabyss.mobzy.mobs

import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.mobzy.api.nms.aliases.NMSDataContainer
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.debug
import com.mineinabyss.mobzy.api.pathfindergoals.Navigation
import com.mineinabyss.mobzy.registration.MobzyTemplates
import net.minecraft.server.v1_16_R1.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.SoundCategory
import org.bukkit.craftbukkit.v1_16_R1.event.CraftEventFactory
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random


/**
 * @property killScore The score with which a player should be rewarded with when the current entity is killed.
 * @property killer The killer of the current entity if it has one.
 * @property scoreboardDisplayNameMZ Used to change the name displayed in the death message.
 * @property template This entity types's [MobTemplate] describing information about entities of this time. It is
 * immutable and not unique to this specific entity.
 */
interface CustomMob {
    // ========== Useful properties ===============
    val nmsEntity: EntityLiving
    val entity get() = nmsEntity.bukkitEntity as LivingEntity
    val template: MobTemplate get() = MobzyTemplates[nmsEntity]
    val locX get() = entity.location.x
    val locY get() = entity.location.y
    val locZ get() = entity.location.z
    private val world: World get() = entity.world.toNMS()
    private val location: Location get() = entity.location
    val navigation get() = Navigation((nmsEntity as EntityInsentient).navigation, nmsEntity as EntityInsentient)
    val killer: EntityHuman? get() = nmsEntity.killer

    val scoreboardDisplayNameMZ: ChatMessage get() = ChatMessage(template.name.split('_').joinToString(" ") { it.capitalize() })

    // ========== Things to be implemented ==========
    val soundAmbient: String?
        get() = null
    val soundHurt: String? get() = null
    val soundDeath: String? get() = null
    val soundStep: String? get() = null
    var dead: Boolean
    val killScore: Int

    fun createPathfinders()
    fun lastDamageByPlayerTime(): Int
    fun saveMobNBT(nbttagcompound: NMSDataContainer) = Unit
    fun loadMobNBT(nbttagcompound: NMSDataContainer) = Unit

    fun onRightClick(player: Player) {}

    fun dropExp()

    // ========== Pre-written behaviour ============
    /**
     * Applies some default attributes that every custom mob should have, such as a model, invisibility, and an
     * identifier scoreboard tag
     */
    fun createFromBase() {
        entity.addScoreboardTag("customMob3")
        entity.addScoreboardTag(template.name)

        //create an item based on model ID in head slot if entity will be using itself for the model
        entity.equipment!!.helmet = template.modelItemStack
        entity.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1, false, false))
    }

    fun dieCM(damageSource: DamageSource?) {
        if (!dead) {
//            killedMZ = true
            val killer = killer
            debug("&c${template.name} died at coords ${locX.toInt()} ${locY.toInt()} ${locZ.toInt()}".color())
            if (killScore >= 0 && killer != null) killer.a(nmsEntity, killScore, damageSource)
            // this line causes the entity to send a statistics update on death (we don't want this as it causes a NPE exception and crash)
//            killer?.a_(nmsEntity);

            if (entity.isSleeping) nmsEntity.entityWakeup()

            if (!nmsEntity.world.isClientSide) {
                if (world.gameRules.getBoolean(GameRules.DO_MOB_LOOT) && killer != null) {
                    dropItems(killer.bukkitEntity)
                } else CraftEventFactory.callEntityDeathEvent(nmsEntity)
            }
            nmsEntity.combatTracker.g() //resets combat tracker

            world.broadcastEntityEffect(nmsEntity, 3.toByte())
            nmsEntity.pose = EntityPose.DYING
            //TODO add PlaceHolderAPI support
            template.deathCommands.forEach { Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), it) }
        }
    }

    fun dropItems(killer: HumanEntity){
        val heldItem = killer.inventory.itemInMainHand
        val looting = heldItem.enchantments[Enchantment.LOOT_BONUS_MOBS] ?: 0
        val fireAspect = heldItem.enchantments[Enchantment.FIRE_ASPECT] ?: 0
        CraftEventFactory.callEntityDeathEvent(nmsEntity, template.chooseDrops(looting, fireAspect))
        nmsEntity.expToDrop = expToDrop()
        dropExp()
    }

    fun expToDrop(): Int {
        return if (template.minExp == null || template.maxExp == null) nmsEntity.expToDrop
        else if (template.maxExp!! <= template.minExp!!) template.minExp!!
        else Random.nextInt(template.minExp!!, template.maxExp!!)
    }


    fun makeSound(sound: String?) {
        if (sound != null)
            entity.world.playSound(location, sound, SoundCategory.NEUTRAL, 1f, (Random.nextDouble(1.0, 1.02).toFloat()))
    }

    // ========== Helper methods ===================

    fun randomSound(vararg sounds: String) = sounds.random()
}