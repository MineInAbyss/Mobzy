package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.looty.ecs.components.Screaming

object ScreamingSystem: TickingSystem(interval = 50){
    override fun tick() = Engine.runFor<Screaming> { (message) ->
        println("I am screaming $message")
    }
}