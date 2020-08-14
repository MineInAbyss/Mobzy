package com.mineinabyss.mobzy.registration

import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.configuration.MobTypeConfigs
import com.mineinabyss.mobzy.ecs.behaviors.TemptBehavior
import com.mineinabyss.mobzy.ecs.components.PathfinderComponent
import com.mineinabyss.mobzy.ecs.components.MobzyComponent
import com.mineinabyss.mobzy.ecs.components.Model
import com.mineinabyss.mobzy.ecs.components.SerializableComponent
import com.mineinabyss.mobzy.ecs.components.minecraft.DeathLoot
import com.mineinabyss.mobzy.ecs.components.minecraft.EntityComponent
import com.mineinabyss.mobzy.ecs.components.minecraft.MobAttributes
import com.mineinabyss.mobzy.ecs.events.EntityCreatedEvent
import com.mineinabyss.mobzy.ecs.systems.Engine
import com.mineinabyss.mobzy.ecs.systems.TemptSystem
import com.mineinabyss.mobzy.ecs.systems.WalkingAnimationSystem
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.modules.SerializersModule
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

internal object MobzyECSRegistry : Listener {
    @EventHandler
    fun attachPathfindersOnEntityCreatedEvent(event: EntityCreatedEvent) {
        val mob = Engine.get<EntityComponent>(event.id)?.entity ?: return
        mob.type.behaviors.forEach { (priority, component) ->
            mob.nmsEntity.addPathfinderGoal(priority, component.createPathfinder(mob))
        }
    }

    @ImplicitReflectionSerializer
    fun register(){
        registerSystems()
        registerComponentSerialization()
    }

    fun registerSystems() {
        Engine.registeredSystems += WalkingAnimationSystem
        Engine.registeredSystems += TemptSystem
    }

    @ImplicitReflectionSerializer
    fun registerComponentSerialization() {
        //TODO annotate serializable components to register this automatically
        MobTypeConfigs.addSerializerModule(SerializersModule {
            polymorphic<MobzyComponent> {
                subclass<MobAttributes>()
                subclass<DeathLoot>()
                subclass<Model>()
                subclass<TemptBehavior>()
            }
            polymorphic<SerializableComponent> {
            }
            polymorphic<PathfinderComponent> {
            }
        })
    }
}