package com.mineinabyss.mobzy.ecs

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.geary.ecs.components.addComponents
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.ecs.components.has
import com.mineinabyss.geary.ecs.components.with
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.entity
import com.mineinabyss.geary.ecs.events.EntityRemovedEvent
import com.mineinabyss.geary.ecs.remove
import com.mineinabyss.looty.ecs.components.Inventory
import com.mineinabyss.looty.ecs.components.PlayerComponent
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem
import com.mineinabyss.mobzy.api.toMobzy
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import kotlin.collections.set

object BukkitEntityAccess: Listener {
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
        val gearyPlayer = geary(player) ?: return
        ItemTrackerSystem.apply {
            val inventory = player.get<Inventory>() ?: return
            gearyPlayer.updateAndSaveItems(player, inventory)
            inventory.unregisterAll()
        }
        gearyPlayer.remove()
        removeEntity(player)
    }

    @EventHandler
    fun onEntityRemoved(e: EntityRemovedEvent) {
        //clear itself from parent and children
        e.entity.apply {
            with<PlayerComponent> { (player) ->
                removeEntity(player)
            } //TODO might be better to move elsewhere, also handle for all bukkit entities
        }
    }
}

fun geary(entity: Entity): GearyEntity? = entity.toMobzy() ?: BukkitEntityAccess.getEntity(entity)

//TODO add the rest of the GearyEntity operations here
inline fun <reified T : MobzyComponent> Entity.get(): T? = geary(this)?.get()

inline fun <reified T : MobzyComponent> Entity.has(): Boolean = geary(this)?.has<T>() ?: false