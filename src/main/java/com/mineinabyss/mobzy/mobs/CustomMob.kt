package com.mineinabyss.mobzy.mobs

import com.mineinabyss.mobzy.api.nms.aliases.NMSDataContainer
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityInsentient
import com.mineinabyss.mobzy.api.nms.aliases.living
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.Navigation
import com.mineinabyss.mobzy.ecs.components.minecraft.EntityComponent
import com.mineinabyss.mobzy.ecs.components.minecraft.deathLoot
import com.mineinabyss.mobzy.ecs.components.model
import com.mineinabyss.mobzy.ecs.events.EntityCreatedEvent
import com.mineinabyss.mobzy.ecs.systems.addComponent
import com.mineinabyss.mobzy.registration.MobTypes
import net.minecraft.server.v1_16_R1.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.SoundCategory
import org.bukkit.craftbukkit.v1_16_R1.event.CraftEventFactory
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

typealias AnyCustomMob = CustomMob<*>


/**
 * @property killScore The score with which a player should be rewarded with when the current entity is killed.
 * @property killer The killer of the current entity if it has one.
 * @property scoreboardDisplayNameMZ Used to change the name displayed in the death message.
 * @property type This entity types's [MobType] describing information about entities of this time. It is
 * immutable and not unique to this specific entity.
 */
interface CustomMob<E : Mob> {
    val mobzyId: Int
    // ========== Useful properties ===============
    val nmsEntity: EntityInsentient
    @Suppress("UNCHECKED_CAST")
    val entity get() = nmsEntity.bukkitEntity as E

    val type: MobType get() = MobTypes[this] //TODO

    val locX get() = entity.location.x
    val locY get() = entity.location.y
    val locZ get() = entity.location.z
    private val nmsWorld: World get() = entity.world.toNMS()
    private val location: Location get() = entity.location
    val navigation get() = Navigation(nmsEntity.navigation, nmsEntity)
    val killer: EntityHuman? get() = nmsEntity.killer

    val scoreboardDisplayNameMZ: ChatMessage get() = ChatMessage(type.name.split('_').joinToString(" ") { it.capitalize() })

    var target
        get() = nmsEntity.goalTarget?.living
        set(value) {
            nmsEntity.goalTarget = value?.toNMS<NMSEntityInsentient>()
        }

    // ========== Things to be implemented ==========
    val soundAmbient: String? get() = null
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
        entity.addScoreboardTag(type.name)
        SystemManager.runOn(this)

        addComponent(EntityComponent(this))
        type.behaviors.forEach {(_, component) -> //TODO unify these into one
            addComponent(component)
        }
        type.staticComponents.forEach {(_, component) ->
            addComponent(component)
        }
        type.components.forEach { (_, component) ->
            addComponent(component.copy())
        }
        EntityCreatedEvent(mobzyId).callEvent()


        //create an item based on model ID in head slot if entity will be using itself for the model
        type.model?.apply { entity.equipment!!.helmet = modelItemStack }
        entity.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1, false, false))
    }

    fun dieCM(damageSource: DamageSource?) {
        if (!dead) {
            dead = true
            val killer = killer
            if (killScore >= 0 && killer != null) killer.a(nmsEntity, killScore, damageSource)
            // this line causes the entity to send a statistics update on death (we don't want this as it causes a NPE exception and crash)
//            killer?.a_(nmsEntity);

            if (entity.isSleeping) nmsEntity.entityWakeup()

            if (!nmsEntity.world.isClientSide) {
                if (nmsWorld.gameRules.getBoolean(GameRules.DO_MOB_LOOT) && killer != null) {
                    dropItems(killer.bukkitEntity)
                } else CraftEventFactory.callEntityDeathEvent(nmsEntity)
            }
            nmsEntity.combatTracker.g() //resets combat tracker

            nmsWorld.broadcastEntityEffect(nmsEntity, 3.toByte())
            nmsEntity.pose = EntityPose.DYING
            //TODO add PlaceHolderAPI support
            type.deathLoot?.deathCommands?.forEach { Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), it) }
        }
    }

    fun dropItems(killer: HumanEntity) {
        val heldItem = killer.inventory.itemInMainHand
        val looting = heldItem.enchantments[Enchantment.LOOT_BONUS_MOBS] ?: 0
        val fireAspect = heldItem.enchantments[Enchantment.FIRE_ASPECT] ?: 0
        CraftEventFactory.callEntityDeathEvent(nmsEntity, type.chooseDrops(looting, fireAspect))
        nmsEntity.expToDrop = expToDrop()
        dropExp()
    }

    fun expToDrop(): Int = when { //TODO move into system
        type.deathLoot?.minExp == null || type.deathLoot?.maxExp == null -> nmsEntity.expToDrop
        type.deathLoot?.maxExp!! <= type.deathLoot?.minExp!! -> type.deathLoot?.minExp!!
        else -> Random.nextInt(type.deathLoot?.minExp!!, type.deathLoot?.maxExp!!)
    }

    @Suppress("UNREACHABLE_CODE")
    fun makeSound(sound: String?) {
        if (sound != null)
            entity.world.playSound(location, sound, SoundCategory.NEUTRAL, 1f, (Random.nextDouble(1.0, 1.02).toFloat()))
    }

    // ========== Helper methods ===================

    fun randomSound(vararg sounds: String) = sounds.random()
}