package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.types.GearyEntityType
import com.mineinabyss.geary.minecraft.store.decodeComponents
import com.mineinabyss.idofront.events.call
import com.mineinabyss.mobzy.api.nms.aliases.*
import com.mineinabyss.mobzy.ecs.components.initialization.ItemModel
import com.mineinabyss.mobzy.ecs.events.MobLoadEvent
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.registration.MobzyTypes
import net.minecraft.server.v1_16_R2.EntityProjectileThrowable
import net.minecraft.server.v1_16_R2.EntityTypes
import net.minecraft.server.v1_16_R2.Item
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack
import org.bukkit.entity.HumanEntity
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class ProjectileEntity(
    type: NMSEntityType<*>,
    world: NMSWorld
) : EntityProjectileThrowable(type as EntityTypes<EntityProjectileThrowable>, world), CustomMob {
    override val gearyId: Int = Engine.getNextId()

    override val nmsEntity: NMSEntityInsentient
        get() = TODO("Not yet implemented")

    //override val type: MobType = MobzyTypes[this as CustomMob]
    override val type: MobType = MobzyTypes[this as NMSEntity]

    override fun getPersistentDataContainer(): PersistentDataContainer = object : PersistentDataContainer {
        override fun <T : Any?, Z : Any?> set(key: NamespacedKey, type: PersistentDataType<T, Z>, value: Z) {
            return
        }

        override fun <T : Any?, Z : Any?> has(key: NamespacedKey, type: PersistentDataType<T, Z>): Boolean {
            return false
        }

        override fun <T : Any?, Z : Any?> get(key: NamespacedKey, type: PersistentDataType<T, Z>): Z? {
            return null
        }

        override fun <T : Any?, Z : Any?> getOrDefault(
            key: NamespacedKey,
            type: PersistentDataType<T, Z>,
            defaultValue: Z
        ): Z {
            return defaultValue
        }

        override fun getKeys(): MutableSet<NamespacedKey> {
            return mutableSetOf()
        }

        override fun remove(key: NamespacedKey) {
            return
        }

        override fun isEmpty(): Boolean {
            return true
        }

        override fun getAdapterContext(): PersistentDataAdapterContext {
            return this.adapterContext
        }
    }

    override var dead: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override val killScore: Int
        get() = TODO("Not yet implemented")

    override fun createPathfinders() {
        TODO("Not yet implemented")
    }

    override fun lastDamageByPlayerTime(): Int {
        TODO("Not yet implemented")
    }

    override fun saveMobNBT(nbttagcompound: NMSDataContainer) {
        TODO("Not yet implemented")
    }

    override fun loadMobNBT(nbttagcompound: NMSDataContainer) {
        TODO("Not yet implemented")
    }

    override fun dropExp() {
        TODO("Not yet implemented")
    }

    override fun onPlayerInteract(player: HumanEntity, enumhand: NMSHand): NMSInteractionResult {
        TODO("Not yet implemented")
    }

    override fun getDefaultItem(): Item {
        TODO("Not yet implemented")
    }

    init {
        //TODO: Replace with proper inherited init function after MobBase becomes EntityBase
        //val bukkitEntity = toBukkit()
        //val bukkitEntity = (this as NMSEntity).toBukkit()

        //add type under the generic component since getting components doesn't
        addComponent<GearyEntityType>(this.type)
        //adding components from the type to this entity
        decodeComponents()
        //addComponent(BukkitEntityComponent(bukkitEntity.uniqueId, bukkitEntity))

        //the number is literally just for migrations. Once we figure out how we do that for ecs components, we should
        // use the same system here.
        entity.addScoreboardTag("customEntity3")
        entity.addScoreboardTag(this.type.name)

        MobLoadEvent(this).call()

        item = CraftItemStack.asNMSCopy(get<ItemModel>()?.item?.toItemStack())

        //world.addEntity(this)
    }
}