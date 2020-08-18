package com.mineinabyss.mobzy.registration

import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.configuration.MobTypeConfigs
import com.mineinabyss.mobzy.ecs.pathfinders.TemptBehavior
import com.mineinabyss.mobzy.ecs.components.*
import com.mineinabyss.mobzy.ecs.components.minecraft.DeathLoot
import com.mineinabyss.mobzy.ecs.components.minecraft.MobComponent
import com.mineinabyss.mobzy.ecs.components.minecraft.MobAttributes
import com.mineinabyss.mobzy.ecs.events.EntityCreatedEvent
import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.geary.ecs.CopyableComponent
import com.mineinabyss.mobzy.ecs.systems.TemptSystem
import com.mineinabyss.mobzy.ecs.systems.WalkingAnimationSystem
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.modules.SerializersModule
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

internal object MobzyECSRegistry : Listener {
    @EventHandler
    fun attachPathfindersOnEntityCreatedEvent(event: EntityCreatedEvent) {
        val mob = Engine.get<MobComponent>(event.id)?.mob ?: return
        val pathfinders = Engine.get<Pathfinders>(event.id)?.pathfinders ?: return
        pathfinders.forEach { (priority, component) ->
            mob.toNMS().addPathfinderGoal(priority, component.createPathfinder(mob))
            Engine.addComponent(event.id, component)
        }
    }

    @ImplicitReflectionSerializer
    fun register() {
        registerSystems()
        registerComponentSerialization()
    }

    private fun registerSystems() {
        Engine.addSystems(
                WalkingAnimationSystem, TemptSystem
        )
    }

    @ImplicitReflectionSerializer
    private fun registerComponentSerialization() {
        //TODO annotate serializable components to register this automatically
        MobTypeConfigs.addSerializerModule(SerializersModule {
            polymorphic<MobzyComponent> {
                subclass<MobAttributes>()
                subclass<DeathLoot>()
                subclass<Model>()
                subclass<TemptBehavior>()
                subclass<Pathfinders>()
            }
            polymorphic<CopyableComponent> {
            }
            polymorphic<PathfinderComponent> {
            }
        })
    }
}