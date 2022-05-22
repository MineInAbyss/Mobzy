package com.mineinabyss.mobzy.systems.listeners

import com.mineinabyss.geary.helpers.with
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.geary.papermc.access.toGearyOrNull
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.ecs.components.initialization.ModelEngineComponent
import com.mineinabyss.mobzy.ecs.components.interaction.Rideable
import com.mineinabyss.mobzy.ecs.components.interaction.Tamable
import com.mineinabyss.mobzy.ecs.components.interaction.Tamed
import com.mineinabyss.mobzy.systems.systems.ModelEngineSystem.toModelEntity
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import kotlin.random.Random

object TamableListener : Listener {

    /** Tame entities with [Tamable] component on right click */
    @EventHandler
    fun PlayerInteractEntityEvent.tameMob() {
        val mob = (rightClicked as? LivingEntity) ?: return
        val gearyEntity = rightClicked.toGearyOrNull() ?: return
        val maxHealth = gearyEntity.get<MobAttributes>()?.maxHealth ?: 20.0
        val modelEntity = rightClicked.toModelEntity() ?: return
        val itemInHand = player.inventory.itemInMainHand

        gearyEntity.with { tamable: Tamable, rideable: Rideable ->
            val tamed = gearyEntity.get<Tamed>() ?: run {
                val random = Random(1).nextDouble()
                if (tamable.tameItem?.toItemStack() == itemInHand) {
                    gearyEntity.setPersisting(Tamed)
                    gearyEntity.get<Tamed>()?.owner = player.uniqueId
                    player.spawnParticle(
                        Particle.HEART,
                        rightClicked.location.apply { y += 1.5 },
                        10,
                        random,
                        random,
                        random
                    )
                }
                return
            }
            when {
                tamable.tameItem?.toItemStack() == itemInHand -> {
                    if (mob.health <= maxHealth) {
                        if (mob.health + 2 <= maxHealth) mob.health += 2 else mob.health = maxHealth
                        player.playSound(mob.location, Sound.ENTITY_HORSE_EAT, 1f, 1f)
                        player.spawnParticle(Particle.HEART, rightClicked.location.apply { y += 2 }, 4)
                    } else {
                        val random = Random(1).nextDouble()
                        player.spawnParticle(
                            Particle.HEART,
                            rightClicked.location.apply { y += 1.5 },
                            10,
                            random,
                            random,
                            random
                        )
                    }
                }
                tamed.owner != player.uniqueId -> return
                itemInHand.type == Material.NAME_TAG -> {
                    modelEntity.nametagHandler.setCustomName("nametag", itemInHand.itemMeta.displayName)
                    modelEntity.nametagHandler.setCustomNameVisibility("nametag", true)
                }
                player.isSneaking -> {
                    val model = gearyEntity.get<ModelEngineComponent>() ?: return
                    val saddle = modelEntity.getActiveModel(model.modelId).getPartEntity("saddle")
                    if (rideable.isSaddled) {
                        rightClicked.toGeary().setPersisting(!rideable.isSaddled)
                        if (saddle.isVisible) saddle.setItemVisibility(rideable.isSaddled)
                    } else {
                        if (saddle.isVisible) saddle.setItemVisibility(false)
                        else saddle.setItemVisibility(true)
                    }
                }
            }
        }
    }
}
