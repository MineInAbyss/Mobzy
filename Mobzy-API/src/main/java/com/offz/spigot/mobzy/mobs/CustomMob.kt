package com.offz.spigot.mobzy.mobs

import com.offz.spigot.mobzy.CustomType
import com.offz.spigot.mobzy.debug
import net.minecraft.server.v1_15_R1.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld
import org.bukkit.craftbukkit.v1_15_R1.event.CraftEventFactory
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import java.util.*

interface CustomMob {
    val entity: EntityLiving
    val living: LivingEntity
        get() = entity.bukkitEntity as LivingEntity
    val builder: MobTemplate
    val staticTemplate: MobTemplate
        get() = CustomType.getTemplate(entity.entityType) //FIXME
    val x: Double
        get() = living.location.x

    val y: Double
        get() = living.location.y

    val z: Double
        get() = living.location.z

    private val world: World
        get() = (living.world as CraftWorld).handle

    private val location: Location
        get() = living.location

    fun createPathfinders()

    val soundAmbient: String?
    val soundHurt: String?
    val soundDeath: String?
    val soundStep: String?

    fun randomSound(vararg sounds: String?): String? {
        return sounds[Random().nextInt(sounds.size)]
    }

    var killed: Boolean
    fun lastDamageByPlayerTime(): Int
    fun saveMobNBT(nbttagcompound: NBTTagCompound?)
    fun loadMobNBT(nbttagcompound: NBTTagCompound?)
    /**
     * I suspect EntityLiving's "be" to be this, as Entity#die(DamageSource) calls Entity#a(Entity, int, DamageSource),
     * which gets overriden by EntityPlayer to call EntityHuman#addScore(int) with the passed int.
     *
     * @return The score with which a player should be rewarded with when the current entity is killed.
     */
    val killScore: Int

    /**
     * TODO Not 100% confident about this one, but the way Entity#die(DamageSource) uses it, makes sense for cv() to return the killer
     *
     * @return The killer of the current entity if it has one.
     */
    val killer: EntityLiving?
        get() = entity.killer

    /**
     * @param other Another entity.
     * @return The distance between the current entity and other entity's locations.
     */
    fun distanceToEntity(other: Entity): Double {
        return location.distance(other.location)
    }

/*/**
 * Looks at a target.
 *
 * @param target The target to look at.
 */
fun lookAt(target:ntity) {
    val location = target.location
    lookAt(location.x, location.y, location.z)
}*/
/*
fun lookAt(target: net.minecraft.server.v1_15_R1.Entity) {
    lookAt(target.bukkitEntity)
}

fun lookAt(x: Double, y: Double, z: Double) {
    val dX = x - this.x
    val dZ = z - this.z
    entity.yaw = -(MathHelper.c(dX, dZ) as Float) * 57.295776f
    entity.aQ = entity.yaw
}*/

    fun onRightClick(player: EntityHuman?) {}

/*default Model spawnModel(World world, MobBuilder builder, EntityLiving entity) {
    if (!(entity instanceof SecondaryModelBehaviour))
        return null;

    Model model = new Model(world);
    model.setPosition(entity.locX, entity.locY, entity.locZ);

    //create an item based on model ID in head slot
    org.bukkit.inventory.ItemStack is = new ItemStack(builder.getModelMaterial());
    is.setDurability((short) builder.getModelID());
    ItemMeta meta = is.getItemMeta();
    meta.setUnbreakable(true);
    is.setItemMeta(meta);
    ((LivingEntity) model.getBukkitEntity()).getEquipment().setHelmet(is);
    model.setInvisible(true);
    model.setMarker(true);
    model.setSilent(true);
    model.addScoreboardTag("additionalPart");

//        EntityArmorStand empty = new EntityArmorStand(world);
//        empty.setPosition(entity.locX, entity.locY, entity.locZ);
//        empty.addScoreboardTag("additionalPart");

    entity.passengers.add(model);
//        empty.passengers.add(model);

//        world.addEntity(empty);
    world.addEntity(model);
    return model;
}*/

    fun dieCM(damageSource: DamageSource?) {
        if (!killed) {
            killed = true
            debug("${ChatColor.RED}${builder.name} died at coords ${x.toInt()} ${y.toInt()} ${z.toInt()}")
            if (killScore >= 0 && killer != null) killer!!.a(entity, killScore, damageSource)
            //this line causes the entity to send a statistics update on death (we don't want this as it causes a NPE exception and crash)
/*if (entity != null)
                entity.b(this);*/
            if (!entity.world.isClientSide) {
                if (world.gameRules.getBoolean(GameRules.DO_MOB_LOOT)) {
                    CraftEventFactory.callEntityDeathEvent(entity, builder.chooseDrops())
                } else {
                    CraftEventFactory.callEntityDeathEvent(entity)
                }
            }
            world.broadcastEntityEffect(entity, 3.toByte())
            //TODO add PlaceHolderAPI support
            builder.deathCommands.forEach { Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), it) }
//            entity.setPose(EntityPose.DYING)
        }
    }

    fun registerPathfinderGoal(priority: Int, goal: PathfinderGoal) {
        (entity as EntityInsentient).goalSelector.a(priority, goal)
    }
}