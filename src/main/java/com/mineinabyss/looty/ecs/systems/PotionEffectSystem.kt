package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.components.Parent
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.forEach
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.looty.ecs.components.PlayerComponent
import com.mineinabyss.looty.ecs.components.PotionComponent
import org.bukkit.potion.PotionEffect

object PotionEffectSystem : TickingSystem(interval = 5) {
    override fun tick() = Engine.forEach<PotionComponent, Parent> { (effect, level), (parent) ->
        val (player) = parent?.get<PlayerComponent>() ?: return@forEach //TODO EntityComponent
        player.addPotionEffect(PotionEffect(effect, 10, level, true))
    }
}