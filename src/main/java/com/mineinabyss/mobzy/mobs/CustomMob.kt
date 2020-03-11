package com.mineinabyss.mobzy.mobs

import com.mineinabyss.mobzy.debug
import com.mineinabyss.mobzy.mobTemplate
import com.mineinabyss.mobzy.mobs.types.FlyingMob
import com.mineinabyss.mobzy.pathfinders.Navigation
import net.minecraft.server.v1_15_R1.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.SoundCategory
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld
import org.bukkit.craftbukkit.v1_15_R1.event.CraftEventFactory
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random


/**
 * @property killScore The score with which a player should be rewarded with when the current entity is killed.
 * @property killer The killer of the current entity if it has one.
 * @property scoreboardDisplayNameMZ Used to change the name displayed in the death message
 */
interface CustomMob {
    // ========== Useful properties ===============
    val entity: EntityLiving
    val living: LivingEntity get() = entity.bukkitEntity as LivingEntity
    val template: MobTemplate
    val staticTemplate: MobTemplate get() = entity.entityType.mobTemplate
    val x: Double get() = living.location.x
    val y: Double get() = living.location.y
    val z: Double get() = living.location.z
    private val world: World get() = (living.world as CraftWorld).handle
    private val location: Location get() = living.location
    val navigation: Navigation
        get() = Navigation((entity as EntityInsentient).navigation, entity as EntityInsentient)
    val killer: EntityHuman? get() = entity.killer
    fun expToDrop(): Int {
        return if (template.minExp == null || template.maxExp == null) entity.expToDrop
        else if (template.maxExp!! <= template.minExp!!) template.minExp!!
        else Random.nextInt(template.minExp!!, template.maxExp!!)
    }

    val scoreboardDisplayNameMZ: ChatMessage
        get() = ChatMessage(template.name.split('_').joinToString(" ") { it.capitalize() })

    // ========== Things to be overloaded ==========
    val soundAmbient: String?
        get() = null
    val soundHurt: String? get() = null
    val soundDeath: String? get() = null
    val soundStep: String? get() = null
    var killedMZ: Boolean
    val killScore: Int

    fun createPathfinders()
    fun lastDamageByPlayerTime(): Int
    fun saveMobNBT(nbttagcompound: NBTTagCompound?)
    fun loadMobNBT(nbttagcompound: NBTTagCompound?)

    fun onRightClick(player: EntityHuman) {}

    fun dropExp()

    // ========== Pre-written behaviour ============
    /**
     * Applies some default attributes that every custom mob should have, such as a model, invisibility, and an
     * identifier scoreboard tag
     */
    fun createFromBase() {
//        entity.expToDrop = 3
        living.addScoreboardTag("customMob2")
        living.addScoreboardTag(template.name)

        //create an item based on model ID in head slot if entity will be using itself for the model
        living.equipment!!.helmet = template.modelItemStack
        living.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1, false, false))

        //disguise the entity
//        DisguiseAPI.disguiseEntity(entity.bukkitEntity, MobDisguise(template.disguiseAs, template.isAdult).also { it.watcher.isInvisible = true })
    }

    fun setConfiguredAttributes() {
        staticTemplate.maxHealth?.let { entity.getAttributeInstance(GenericAttributes.MAX_HEALTH).value = it }
        staticTemplate.movementSpeed?.let { entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).value = it }
        if (this !is FlyingMob) //flying mobs can't have an attack damage attribute, we use the builder's value instead
            staticTemplate.attackDamage?.let { entity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).value = it }
        staticTemplate.followRange?.let { entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).value = it }
    }

    fun dieCM(damageSource: DamageSource?) {
        if (!killedMZ) {
            killedMZ = true
            debug("${ChatColor.RED}${template.name} died at coords ${x.toInt()} ${y.toInt()} ${z.toInt()}")
            if (killScore >= 0 && killer != null) killer!!.a(entity, killScore, damageSource)
            // this line causes the entity to send a statistics update on death (we don't want this as it causes a NPE exception and crash)
//            if (entity != null) entity.b(this);

            if (entity.isSleeping) entity.entityWakeup()

            if (!entity.world.isClientSide) {
                if (world.gameRules.getBoolean(GameRules.DO_MOB_LOOT)) {
                    val killer = killer?.bukkitEntity
                    val looting = killer?.inventory?.itemInMainHand?.enchantments?.get(Enchantment.LOOT_BONUS_MOBS) ?: 0
                    CraftEventFactory.callEntityDeathEvent(entity, template.chooseDrops(looting))
                    entity.expToDrop = expToDrop()
                    dropExp()
                } else CraftEventFactory.callEntityDeathEvent(entity)
            }

            world.broadcastEntityEffect(entity, 3.toByte())
//            entity.setPose(EntityPose.DYING)
            //TODO add PlaceHolderAPI support
            template.deathCommands.forEach { Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), it) }
        }
    }

    fun makeSound(sound: String?) {
        if (sound != null)
            living.world.playSound(location, sound, SoundCategory.NEUTRAL, 1f, (Random.nextDouble(1.0, 1.02).toFloat()))
    }

    // ========== Helper methods ===================
    fun addPathfinderGoal(priority: Int, goal: PathfinderGoal) {
        (entity as EntityInsentient).goalSelector.a(priority, goal)
    }

    fun addTargetSelector(priority: Int, goal: PathfinderGoalTarget) {
        (entity as EntityInsentient).targetSelector.a(priority, goal)
    }

    fun randomSound(vararg sounds: String?): String? = sounds[Random.nextInt(sounds.size)]

    /**
     * @param other Another entity.
     * @return The distance between the current entity and other entity's locations.
     */
    fun distanceTo(other: Entity): Double = distanceTo(other.location)

    /**
     * @param other Some location
     * @return The distance between the current entity and the other location
     */
    fun distanceTo(other: Location): Double = location.distance(other)

    /**
     * @param range the range to search within
     * @return a nearby player, or null if none are in the range
     */
    fun findNearbyPlayer(range: Double) = world.findNearbyPlayer(this.entity, range)

    fun lookAt(x: Double, z: Double) = lookAt(x, y, z)

    fun lookAt(x: Double, y: Double, z: Double) {
        val dirBetweenLocations = org.bukkit.util.Vector(x, y, z).subtract(location.toVector())
        val location = living.location
        location.direction = dirBetweenLocations
        living.setRotation(location.yaw, location.pitch)
    }
//            (entity as EntityInsentient).controllerLook.a(x, y, z)

    /**
     * @param location the location to look at
     *
     * Be careful and ensure that the custom mob using this is an [EntityInsentient]
     */
    fun lookAt(location: Location) = lookAt(location.x, location.y, location.z)

    /**
     * @param entity the entity to look at
     *
     * Be careful and ensure that the custom mob using this is an [EntityInsentient]
     */
    fun lookAt(entity: Entity) = lookAt(entity.location)

    fun lookAtPitchLock(location: Location) = lookAt(location.x, location.z)

    fun lookAtPitchLock(entity: Entity) = lookAtPitchLock(entity.location)

    fun canReach(target: Entity) = distanceTo(target) < entity.width / 2 + 1

//    fun jump() = (entity as EntityInsentient).controllerJump.jump()
}