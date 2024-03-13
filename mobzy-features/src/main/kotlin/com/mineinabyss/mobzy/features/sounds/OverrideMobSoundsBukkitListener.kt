package com.mineinabyss.mobzy.features.sounds

import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.idofront.typealiases.BukkitEntity
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent

class OverrideMobSoundsBukkitListener : Listener {
    @EventHandler
    fun EntityDeathEvent.makeSoundOnDeath() {
        val sounds = entity.toGeary().get<Sounds>() ?: return
        makeSound(entity, sounds.death)
    }

    @EventHandler(ignoreCancelled = true)
    fun EntityDamageByEntityEvent.onDamage() {
        val sounds = entity.toGearyOrNull()?.get<Sounds>() ?: return
        makeSound(entity, sounds.hurt)
    }

    companion object {
        fun makeSound(mob: BukkitEntity, sound: Sounds.Sound?) {
            if (sound == null) return
            mob.location.world.playSound(
                Sound.sound(
                    Key.key(sound.sound),
                    sound.category,
                    sound.volume,
                    sound.adjustedPitch()
                ),
                mob
            )
        }
    }
}
