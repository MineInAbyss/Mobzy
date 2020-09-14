package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.forEach
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.looty.ecs.components.Held
import com.mineinabyss.looty.ecs.components.Screaming

object ScreamingSystem: TickingSystem(interval = 50){
    override fun tick() = Engine.forEach <Screaming, Held> { (message), _ ->
        println("I am screaming $message")
    }
}