package com.mineinabyss.mobzy.ecs

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.geary.ecs.components.addComponents
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.ecs.components.has
import com.mineinabyss.geary.ecs.remove
import com.mineinabyss.looty.ecs.components.Inventory
import com.mineinabyss.looty.ecs.components.PlayerComponent
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem
import com.mineinabyss.mobzy.api.toMobzy
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object BukkitEntityAccess {
    val mobMap = mutableMapOf<Entity, GearyEntity>()

    fun registerEntity(entity: Entity, gearyEntity: GearyEntity) {
        mobMap[entity] = gearyEntity
    }

    fun removeEntity(entity: Entity) = mobMap.remove(entity)

    fun getEntity(entity: Entity) = mobMap[entity]
    //TODO a way of getting ID given a vanilla entity as fallback


    fun registerPlayer(player: Player) = registerEntity(player,
            Engine.entity {
                addComponents(setOf(PlayerComponent(player.uniqueId), Inventory()))
            }
    )

    fun unregisterPlayer(player: Player) {
        val gearyPlayer = player.ecs ?: return
        ItemTrackerSystem.apply {
            val inventory = player.get<Inventory>() ?: return
            gearyPlayer.updateAndSaveItems(player, inventory)
            inventory.unregisterAll()
        }
        gearyPlayer.remove()
        removeEntity(player)
    }

}

val Entity.ecs get() = BukkitEntityAccess.getEntity(this)

inline fun <reified T : MobzyComponent> Entity.get(): T? = toMobzy()?.let { Engine.getFor(it.gearyId) } ?: ecs?.get()

inline fun <reified T : MobzyComponent> Entity.has(): Boolean = toMobzy()?.let { Engine.has<T>(it.gearyId) }
        ?: ecs?.has<T>() ?: false