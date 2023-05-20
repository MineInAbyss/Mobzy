package com.mineinabyss.mobzy.features.sounds

import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.idofront.typealiases.BukkitEntity
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent

class OverrideMobSoundsSystem : Listener {
    @EventHandler
    fun EntityDeathEvent.makeSoundOnDeath() {
        val sounds = entity.toGeary().get<Sounds>() ?: return
        makeSound(entity, sounds, sounds.death)
    }

    @EventHandler
    fun EntityDamageByEntityEvent.onDamage() {
        val sounds = entity.toGeary().get<Sounds>() ?: return
        makeSound(entity, sounds, sounds.hurt)
    }

    companion object {
        fun makeSound(mob: BukkitEntity, sounds: Sounds, sound: String?) {
            if (sound == null) return
            val (x, y, z) = mob.location
            mob.getNearbyEntities(32.0, 32.0, 32.0).filterIsInstance<Player>().forEach {
                val dist = mob.location.distance(it.location).toFloat()
                val volume = sounds.volume * ((32F - dist).coerceAtLeast(0F) / 32F)
                it.playSound(
                    Sound.sound(
                        Key.key(sound),
                        Sound.Source.AMBIENT,
                        volume,
                        sounds.adjustedPitch()
                    ), x, y, z
                )
            }
        }
    }
}
