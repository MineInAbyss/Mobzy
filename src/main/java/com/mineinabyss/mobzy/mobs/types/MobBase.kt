package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.ecs.components.with
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.mobzy.api.nms.aliases.*
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.ecs.components.death.DeathLoot
import com.mineinabyss.mobzy.ecs.components.death.expToDrop
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.registration.MobzyTypes
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_16_R2.event.CraftEventFactory
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Mob

abstract class MobBase : NMSEntityInsentient(error(""), error("")), CustomMob {
    final override val entity: Mob get() = super.entity
    final override val gearyId: Int = Engine.getNextId()
    //we get the type via this mob's EntityTypes, later register the type with the ECS
    final override val type: MobType = MobzyTypes[this as CustomMob]

    //implementation of properties from CustomMob
    final override var dead: Boolean by ::killed
    final override val nmsEntity: NMSEntityInsentient get() = this

    final override fun lastDamageByPlayerTime(): Int = lastDamageByPlayerTime
    final override val killScore: Int = 0 //TODO was aV, update

    final override fun dropExp() = dropExperience()

    //overriding NMS functions
    //TODO option to inherit pathfinders from a group
    final override fun initPathfinder() = createPathfinders()
    override fun createPathfinders() = super.initPathfinder()

    final override fun saveData(nbttagcompound: NMSDataContainer) = saveMobNBT(nbttagcompound)
    override fun saveMobNBT(nbttagcompound: NMSDataContainer) {
        //FIXME the rest of mobzy needs to be rewritten to actually make use of these components,
        // we'll keep this disabled in the meantime to not read and write unnecessarily.
//        entity.persistentDataContainer.encodeComponents(getComponents().filter { it.persist })
        super.saveData(nbttagcompound)
    }

    final override fun loadData(nbttagcompound: NMSDataContainer) = loadMobNBT(nbttagcompound)
    override fun loadMobNBT(nbttagcompound: NMSDataContainer) {
        //same story here, no need to load stuff yet.
//        addComponents(entity.persistentDataContainer.decodeComponents())

        //TODO this will replace any components that might have been overridden/removed on purpose, and it won't do it
        // immediately which could cause some confusion. Decide on how we expect static components to work first!
//        addComponents(type.staticComponents)
        super.loadData(nbttagcompound)
    }

    final override fun b(entityhuman: NMSEntityHuman, enumhand: NMSHand): NMSInteractionResult =
            onPlayerInteract(entityhuman.toBukkit(), enumhand)

    override fun onPlayerInteract(player: HumanEntity, enumhand: NMSHand): NMSInteractionResult =
            super.b(player.toNMS(), enumhand)

    override fun die(damagesource: NMSDamageSource) = (this as CustomMob).die(damagesource)
    override fun getScoreboardDisplayName() = scoreboardDisplayNameMZ
    override fun getExpValue(entityhuman: NMSEntityHuman): Int = get<DeathLoot>()?.expToDrop() ?: this.expToDrop

    override fun getSoundVolume(): Float = get<Sounds>()?.volume ?: super.getSoundVolume()
    override fun getSoundAmbient(): NMSSound? = makeSound(super.getSoundAmbient()) { ambient }
    override fun getSoundHurt(damagesource: NMSDamageSource): NMSSound? = makeSound(super.getSoundHurt(damagesource)) { hurt }
    override fun getSoundDeath(): NMSSound? = makeSound(super.getSoundDeath()) { death }
    override fun getSoundSplash(): NMSSound? = makeSound(super.getSoundSplash()) { splash }
    override fun getSoundSwim(): NMSSound? = makeSound(super.getSoundSwim()) { swim }
}

//TODO these should be part of a companion object that doesn't get copied over
fun CustomMob.die(damageSource: NMSDamageSource?) {
    val nmsWorld: NMSWorld = entity.world.toNMS()
    if (!dead) {
        dead = true
        val killer = killer
        if (killScore >= 0 && killer != null) killer.a(nmsEntity, killScore, damageSource)
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
        get<DeathLoot>()?.deathCommands?.forEach { Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), it) }
    }
}

fun CustomMob.dropItems(killer: HumanEntity) {
    val heldItem = killer.inventory.itemInMainHand
    val looting = heldItem.enchantments[Enchantment.LOOT_BONUS_MOBS] ?: 0
    val fire = heldItem.enchantments[Enchantment.FIRE_ASPECT] ?: 0 > 0
    with<DeathLoot> { deathLoot ->
        CraftEventFactory.callEntityDeathEvent(nmsEntity, deathLoot.drops.toList().map { it.chooseDrop(looting, fire) })
        deathLoot.expToDrop()?.let { nmsEntity.expToDrop = it }
    }
    dropExp()
}
