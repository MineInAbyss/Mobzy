package com.mineinabyss.mobzy.features.taming

import com.mineinabyss.geary.helpers.with
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Tameable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import kotlin.random.Random

class TamableListener : Listener {
    /** Tame entities with [Tamable] component on right click */
    @EventHandler
    fun PlayerInteractEntityEvent.tameMob() {
        val mob = (rightClicked as? LivingEntity) ?: return
        val gearyEntity = rightClicked.toGearyOrNull() ?: return
        val maxHealth = mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
        val itemInHand = player.inventory.itemInMainHand

        gearyEntity.with { tamable: Tamable ->
            val tamed = gearyEntity.get<Tamed>() ?: run {
                val random = Random.nextDouble()
                if (tamable.tameItem?.toItemStack() == itemInHand) {
                    gearyEntity.setPersisting(Tamed(owner = player.uniqueId))
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

                player.isSneaking -> {
                    //TODO Fix later
                    /*val model = gearyEntity.get<ModelEngineComponent>() ?: return
                    val saddle = modelEntity.getModel(model.modelId).getBone("saddle").activeModel
                    if (rideable.isSaddled) {
                        rightClicked.toGeary().get<Rideable>()?.isSaddled = !rideable.isSaddled
                        saddle.itemHolderHandler.
                    } else {
                        if (saddle.isVisible) saddle.setItemVisibility(false)
                        else modelEntity.getModel(model.modelId).itemHolderHandler.bones["saddle"]?.itemStack = ItemStack(Material.AIR)
                    }*/
                }

                else -> {}
            }
        }
    }
}
