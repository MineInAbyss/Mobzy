package com.offz.spigot.abyssalcreatures.mobs.hostile

import com.mineinabyss.idofront.items.editItemMeta
import com.offz.spigot.abyssalcreatures.mobs.ThrownRock
import com.offz.spigot.abyssalcreatures.mobs.passive.Neritantan
import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.HostileMob
import net.minecraft.server.v1_15_R1.*
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

class Inbyo(world: World?) : HostileMob(world, "Inbyo"), HitBehaviour, IRangedEntity {
    override fun createPathfinders() {
        super.createPathfinders()
        addTargetSelector(1, PathfinderGoalNearestAttackableTarget(this, Neritantan::class.java, true))
        addPathfinderGoal(1, PathfinderGoalArrowAttack(this, 1.25, 40, 10.0f))
    }

    override fun a(entityliving: EntityLiving, f: Float) {
        val snowball = ThrownRock(world, this)
        val dX = entityliving.locX() - locX()
        val dY = entityliving.headY - 1 - snowball.locY()
        val dZ = entityliving.locZ() - locZ()
        val f1 = MathHelper.sqrt(dX * dX + dZ * dZ) * 0.2
        a(SoundEffects.ENTITY_SNOW_GOLEM_SHOOT, 1.0f, 1.0f / (getRandom().nextFloat() * 0.4f + 0.8f))
        snowball.shoot(dX, dY + f1, dZ, 1.6f, 12.0f)

        val itemStack = ItemStack(Material.SNOWBALL).editItemMeta {
            setCustomModelData(1)
        }
        snowball.setItem(CraftItemStack.asNMSCopy(itemStack))
        world.addEntity(snowball)
    }
}