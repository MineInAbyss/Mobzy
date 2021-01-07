package com.mineinabyss.mobzy.ecs.goals.mobzy.hostile

import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.mobzy.api.helpers.entity.distanceSqrTo
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.doneNavigating
import com.mineinabyss.mobzy.api.pathfindergoals.moveToEntity
import com.mineinabyss.mobzy.api.pathfindergoals.stopNavigation
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.server.v1_16_R2.*
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack
import org.bukkit.entity.Creature
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.inventory.ItemStack
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

@Serializable
@SerialName("mobzy:behavior.throw_items")
class ThrowItemsBehavior(
    @SerialName("item") private val _item: SerializableItemStack,
    val damage: Float,
    val minChaseRad: Double = 0.0,
    val minThrowRad: Double = 7.0,
    val yOffset: Double = 0.0,
    val cooldown: Long = 3000L
) : PathfinderComponent() {
    @Transient
    val item = _item.toItemStack()

    override fun build(mob: Mob) = ThrowItemsGoal(
        (mob as Creature),
        item,
        damage,
        minChaseRad,
        minThrowRad,
        yOffset,
        cooldown
    )
}

/**
 * Throws items at the target
 * @param minChaseRad Will not approach the target closer than this many blocks (unless other pathfinders define further behaviour).
 * @param minThrowRad The minimum radius at which to start throwing item at the target.
 * @param cooldown How long to wait between firing at the target.
 */
class ThrowItemsGoal(
    override val mob: Creature,
    private val item: ItemStack,
    private val damage: Float,
    private val minChaseRad: Double,
    private val minThrowRad: Double,
    private val yOffset: Double = 0.0,
    //TODO val accuracy: Double,
    cooldown: Long = 3000L
) : MobzyPathfinderGoal(cooldown = cooldown) {
    private var distance = 0.0

    override fun shouldExecute(): Boolean {
        return mob.target != null && mob.distanceSqrTo(mob.target ?: return false).also { distance = it } >
                //if there's no minChaseRad, stop pathfinder completely when we can't throw anymore
                (if (minChaseRad <= 0) minThrowRad else min(minChaseRad, minThrowRad)).pow(2)
    }

    override fun shouldKeepExecuting() = shouldExecute() && !navigation.doneNavigating

    override fun init() {
        mob.target?.let { navigation.moveToEntity(it, 1.0) }
    }

    override fun reset() {
        navigation.stopNavigation()
    }

    override fun execute() {
        val target = mob.target ?: return

        if (distance < minChaseRad)
            navigation.stopNavigation()

        if (cooledDown && distance > minThrowRad) {
            restartCooldown()
            throwItem(target)
        }
    }

    //TODO try not to rely on NMS at all here
    /** Throws the mob's defined item at the [target]*/
    fun throwItem(target: LivingEntity) {
        val world = mob.location.world ?: return
        val location = mob.eyeLocation
        val (x, y, z) = location

        //TODO some way to create different types of projectiles
        val projectile = DamagingThrownItem(item, damage, nmsEntity.world, nmsEntity)
        projectile.setPosition(x, y + yOffset, z)

        val targetLoc = target.eyeLocation
        val dX = targetLoc.x - x
        val dY = targetLoc.y - y - 0.4
        val dZ = targetLoc.z - z

        world.playSound(
            mob.location,
            Sound.ENTITY_SNOW_GOLEM_SHOOT,
            1.0f,
            1.0f / (Random.nextDouble(0.8, 1.2).toFloat())
        )
        projectile.shoot(dX, dY, dZ, 1.6f, 12.0f)
        world.toNMS().addEntity(projectile)
    }
}

class DamagingThrownItem(
    item: ItemStack,
    val damage: Float,
    world: World?,
    thrower: EntityLiving
) : EntitySnowball(world, thrower) {
    init {
        this.item = CraftItemStack.asNMSCopy(item)
    }

    override fun a(mop: MovingObjectPosition) {
        super.a(mop)

        if (mop.type == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
            val hit = (mop as MovingObjectPositionEntity).entity
            if (hit is EntityPlayer)
                hit.damageEntity(DamageSource.projectile(this, shooter), damage)
        }
    }
}
